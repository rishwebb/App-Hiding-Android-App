package com.launcherx.di;

import com.launcherx.data.dao.HiddenAppDao;
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
public final class DatabaseModule_ProvideHiddenAppDaoFactory implements Factory<HiddenAppDao> {
  private final Provider<LauncherDatabase> dbProvider;

  public DatabaseModule_ProvideHiddenAppDaoFactory(Provider<LauncherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HiddenAppDao get() {
    return provideHiddenAppDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideHiddenAppDaoFactory create(
      Provider<LauncherDatabase> dbProvider) {
    return new DatabaseModule_ProvideHiddenAppDaoFactory(dbProvider);
  }

  public static HiddenAppDao provideHiddenAppDao(LauncherDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHiddenAppDao(db));
  }
}
