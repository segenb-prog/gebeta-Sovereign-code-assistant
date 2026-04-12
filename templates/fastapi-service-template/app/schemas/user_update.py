from pydantic import BaseModel, EmailStr
from typing import Optional

class UserUpdateRequest(BaseModel):
    """Schema for partial user profile updates (all fields optional)."""
    email: Optional[EmailStr] = None
    full_name: Optional[str] = None
    password: Optional[str] = None
