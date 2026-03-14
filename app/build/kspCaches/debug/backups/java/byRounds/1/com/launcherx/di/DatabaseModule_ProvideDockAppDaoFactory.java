package com.launcherx.di;

import com.launcherx.data.dao.DockAppDao;
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
public final class DatabaseModule_ProvideDockAppDaoFactory implements Factory<DockAppDao> {
  private final Provider<LauncherDatabase> dbProvider;

  public DatabaseModule_ProvideDockAppDaoFactory(Provider<LauncherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DockAppDao get() {
    return provideDockAppDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDockAppDaoFactory create(
      Provider<LauncherDatabase> dbProvider) {
    return new DatabaseModule_ProvideDockAppDaoFactory(dbProvider);
  }

  public static DockAppDao provideDockAppDao(LauncherDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDockAppDao(db));
  }
}
