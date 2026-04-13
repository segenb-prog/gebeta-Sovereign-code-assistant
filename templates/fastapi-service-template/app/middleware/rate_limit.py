Rate limiting middleware for FastAPI.
Protects auth endpoints from brute-force attacks.
"""

from fastapi import HTTPException, Request
from datetime import datetime, timedelta
from collections import defaultdict
import asyncio


class RateLimiter:
    """Simple in-memory rate limiter (single-instance)."""

    def __init__(self):
        self.attempts: dict[str, list[datetime]] = defaultdict(list)
        self.lock = asyncio.Lock()

    async def is_allowed(
        self,
        key: str,
        max_attempts: int = 10,
        window_seconds: int = 60,
    ) -> bool:
        async with self.lock:
            now = datetime.utcnow()
            cutoff = now - timedelta(seconds=window_seconds)
            self.attempts[key] = [a for a in self.attempts[key] if a > cutoff]
            if len(self.attempts[key]) >= max_attempts:
                return False
            self.attempts[key].append(now)
            return True


limiter = RateLimiter()


async def rate_limit_middleware(
    request: Request,
    key: str,
    max_attempts: int = 10,
    window_seconds: int = 60,
) -> None:
    allowed = await limiter.is_allowed(key, max_attempts, window_seconds)
    if not allowed:
        raise HTTPException(
            status_code=429,
            detail="Too many requests. Please try again later.",
            headers={"Retry-After": str(window_seconds)},
        )