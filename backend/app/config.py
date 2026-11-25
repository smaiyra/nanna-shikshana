from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    APP_NAME: str = "Nanna Shikshana API"
    ENVIRONMENT: str = "development"
    DATABASE_URL: str = "sqlite:///./nanna_shikshana.db"
    JWT_SECRET_KEY: str = "dev-only-nanna-shikshana-secret-key-do-not-use-in-prod"
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 10080
    PACKS_STORAGE_DIR: str = "./storage/packs"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    @property
    def packs_path(self) -> Path:
        path = Path(self.PACKS_STORAGE_DIR)
        path.mkdir(parents=True, exist_ok=True)
        return path


settings = Settings()
