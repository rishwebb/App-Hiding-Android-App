package com.launcherx.di;

import com.launcherx.data.dao.WidgetDao;
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
public final class DatabaseModule_ProvideWidgetDaoFactory implements Factory<WidgetDao> {
  private final Provider<LauncherDatabase> dbProvider;

  public DatabaseModule_ProvideWidgetDaoFactory(Provider<LauncherDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WidgetDao get() {
    return provideWidgetDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWidgetDaoFactory create(
      Provider<LauncherDatabase> dbProvider) {
    return new DatabaseModule_ProvideWidgetDaoFactory(dbProvider);
  }

  public static WidgetDao provideWidgetDao(LauncherDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWidgetDao(db));
  }
}
