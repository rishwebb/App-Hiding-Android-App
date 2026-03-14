package com.launcherx;

import android.app.Application;
import com.launcherx.data.repository.LauncherRepository;
import com.launcherx.icons.IconPackManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LauncherViewModel_Factory implements Factory<LauncherViewModel> {
  private final Provider<Application> appProvider;

  private final Provider<LauncherRepository> repositoryProvider;

  private final Provider<IconPackManager> iconPackManagerProvider;

  public LauncherViewModel_Factory(Provider<Application> appProvider,
      Provider<LauncherRepository> repositoryProvider,
      Provider<IconPackManager> iconPackManagerProvider) {
    this.appProvider = appProvider;
    this.repositoryProvider = repositoryProvider;
    this.iconPackManagerProvider = iconPackManagerProvider;
  }

  @Override
  public LauncherViewModel get() {
    return newInstance(appProvider.get(), repositoryProvider.get(), iconPackManagerProvider.get());
  }

  public static LauncherViewModel_Factory create(Provider<Application> appProvider,
      Provider<LauncherRepository> repositoryProvider,
      Provider<IconPackManager> iconPackManagerProvider) {
    return new LauncherViewModel_Factory(appProvider, repositoryProvider, iconPackManagerProvider);
  }

  public static LauncherViewModel newInstance(Application app, LauncherRepository repository,
      IconPackManager iconPackManager) {
    return new LauncherViewModel(app, repository, iconPackManager);
  }
}
