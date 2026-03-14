package com.launcherx.di;

import com.launcherx.data.dao.WallpaperDao;
import com.launcherx.data.db.LauncherDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideWallpaperDaoFactory implements Factory<WallpaperDao> {
  private final Provider<LauncherDatabase> dbProvider;

  public DatabaseModule_ProvideWallpaperDaoFactory(Provider<LauncherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WallpaperDao get() {
    return provideWallpaperDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWallpaperDaoFactory create(
      Provider<LauncherDatabase> dbProvider) {
    return new DatabaseModule_ProvideWallpaperDaoFactory(dbProvider);
  }

  public static WallpaperDao provideWallpaperDao(LauncherDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWallpaperDao(db));
  }
}
