"""
Authentication endpoints with rate limiting and token refresh.
"""

from fastapi import APIRouter, Depends, HTTPException, status, Request
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.database import get_db
from app.models.user import User
from app.schemas.user import UserCreate, UserResponse, Token
from app.schemas.error import ErrorResponse
from app.utils.security import get_password_hash, verify_password, create_access_token
from app.middleware.rate_limit import rate_limit_middleware

router = APIRouter()
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth/login")


def get_client_ip(request: Request) -> str:
    return request.client.host if request.client else "unknown"


@router.post(
    "/auth/register",
    response_model=UserResponse,
    status_code=status.HTTP_201_CREATED,
    responses={
        400: {"model": ErrorResponse},
        429: {"model": ErrorResponse},
    },
)
async def register(
    user_data: UserCreate,
    db: AsyncSession = Depends(get_db),
    request: Request = None,
) -> UserResponse:
    client_ip = get_client_ip(request)
    await rate_limit_middleware(
        request,
        key=f"register:{client_ip}",
        max_attempts=5,
        window_seconds=300,
    )

    result = await db.execute(select(User).where(User.email == user_data.email))
    if result.scalar_one_or_none():
        raise HTTPException(
            status_code=400,
            detail={
                "error_code": "USER_ALREADY_EXISTS",
                "message": f"User with email {user_data.email} already exists",
            },
        )

    new_user = User(
        email=user_data.email,
        hashed_password=get_password_hash(user_data.password),
        full_name=user_data.full_name,
    )
    db.add(new_user)
    await db.commit()
    await db.refresh(new_user)
    return new_user


@router.post(
    "/auth/login",
    response_model=Token,
    responses={
        401: {"model": ErrorResponse},
        429: {"model": ErrorResponse},
    },
)
async def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: AsyncSession = Depends(get_db),
    request: Request = None,
) -> Token:
    client_ip = get_client_ip(request)
    await rate_limit_middleware(
        request,
        key=f"login:{client_ip}",
        max_attempts=10,
        window_seconds=60,
    )

    result = await db.execute(select(User).where(User.email == form_data.username))
    user = result.scalar_one_or_none()

    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=401,
            detail={
                "error_code": "INVALID_CREDENTIALS",
                "message": "Invalid email or password",
            },
            headers={"WWW-Authenticate": "Bearer"},
        )

    access_token = create_access_token(subject=user.id)
    return {"access_token": access_token, "token_type": "bearer"}


@router.post("/auth/refresh", response_model=Token)
async def refresh_token(
    refresh_request: dict,
    db: AsyncSession = Depends(get_db),
) -> Token:
    from jose import jwt, JWTError
    from app.config import settings

    token = refresh_request.get("token")
    if not token:
        raise HTTPException(
            status_code=401,
            detail={"error_code": "NO_TOKEN", "message": "No token provided"},
        )

    try:
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        user_id = payload.get("sub")
        if not user_id:
            raise HTTPException(status_code=401, detail={"error_code": "INVALID_TOKEN", "message": "Invalid token"})
    except JWTError:
        raise HTTPException(status_code=401, detail={"error_code": "INVALID_TOKEN", "message": "Invalid or expired token"})

    result = await db.execute(select(User).where(User.id == int(user_id)))
    user = result.scalar_one_or_none()
    if not user:
        raise HTTPException(status_code=401, detail={"error_code": "USER_NOT_FOUND", "message": "User no longer exists"})

    new_token = create_access_token(subject=user.id)
    return {"access_token": new_token, "token_type": "bearer"}