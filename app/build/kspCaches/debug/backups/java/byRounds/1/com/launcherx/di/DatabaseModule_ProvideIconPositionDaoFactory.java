package com.launcherx.di;

import com.launcherx.data.dao.IconPositionDao;
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
public final class DatabaseModule_ProvideIconPositionDaoFactory implements Factory<IconPositionDao> {
  private final Provider<LauncherDatabase> dbProvider;

  public DatabaseModule_ProvideIconPositionDaoFactory(Provider<LauncherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public IconPositionDao get() {
    return provideIconPositionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideIconPositionDaoFactory create(
      Provider<LauncherDatabase> dbProvider) {
    return new DatabaseModule_ProvideIconPositionDaoFactory(dbProvider);
  }

  public static IconPositionDao provideIconPositionDao(LauncherDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIconPositionDao(db));
  }
}
