"""
Pydantic schemas for request/response validation.
"""

import re
from datetime import datetime
from pydantic import BaseModel, EmailStr, ConfigDict, field_validator


class UserBase(BaseModel):
    """Base user schema."""
    email: EmailStr
    full_name: str | None = None


class UserCreate(UserBase):
    """Schema for user registration."""
    password: str

    @field_validator("password")
    @classmethod
    def validate_password_strength(cls, v: str) -> str:
        """Ensure password meets minimum strength requirements."""
        if len(v) < 8:
            raise ValueError("Password must be at least 8 characters long")
        if not re.search(r'[A-Z]', v):
            raise ValueError("Password must contain at least one uppercase letter")
        if not re.search(r'[a-z]', v):
            raise ValueError("Password must contain at least one lowercase letter")
        if not re.search(r'\d', v):
            raise ValueError("Password must contain at least one digit")
        if not re.search(r'[@$!%*?&#]', v):
            raise ValueError("Password must contain at least one special character (@$!%*?&#)")
        return v


class UserLogin(BaseModel):
    """Schema for user login."""
    email: EmailStr
    password: str


class UserResponse(UserBase):
    """Schema for user response."""
    id: int
    is_active: bool
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)


class Token(BaseModel):
    """Schema for JWT token response."""
    access_token: str
    token_type: str = "bearer"


class TokenPayload(BaseModel):
    """Schema for JWT token payload."""
    sub: str | None = None
    exp: int | None = None
