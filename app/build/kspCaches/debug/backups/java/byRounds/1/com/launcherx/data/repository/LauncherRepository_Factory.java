package com.launcherx.data.repository;

import com.launcherx.data.dao.DockAppDao;
import com.launcherx.data.dao.HiddenAppDao;
import com.launcherx.data.dao.IconPositionDao;
import com.launcherx.data.dao.WallpaperDao;
import com.launcherx.data.dao.WidgetDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LauncherRepository_Factory implements Factory<LauncherRepository> {
  private final Provider<IconPositionDao> iconPositionDaoProvider;

  private final Provider<DockAppDao> dockAppDaoProvider;

  private final Provider<WidgetDao> widgetDaoProvider;

  private final Provider<HiddenAppDao> hiddenAppDaoProvider;

  private final Provider<WallpaperDao> wallpaperDaoProvider;

  public LauncherRepository_Factory(Provider<IconPositionDao> iconPositionDaoProvider,
      Provider<DockAppDao> dockAppDaoProvider, Provider<WidgetDao> widgetDaoProvider,
      Provider<HiddenAppDao> hiddenAppDaoProvider, Provider<WallpaperDao> wallpaperDaoProvider) {
    this.iconPositionDaoProvider = iconPositionDaoProvider;
    this.dockAppDaoProvider = dockAppDaoProvider;
    this.widgetDaoProvider = widgetDaoProvider;
    this.hiddenAppDaoProvider = hiddenAppDaoProvider;
    this.wallpaperDaoProvider = wallpaperDaoProvider;
  }

  @Override
  public LauncherRepository get() {
    return newInstance(iconPositionDaoProvider.get(), dockAppDaoProvider.get(), widgetDaoProvider.get(), hiddenAppDaoProvider.get(), wallpaperDaoProvider.get());
  }

  public static LauncherRepository_Factory create(Provider<IconPositionDao> iconPositionDaoProvider,
      Provider<DockAppDao> dockAppDaoProvider, Provider<WidgetDao> widgetDaoProvider,
      Provider<HiddenAppDao> hiddenAppDaoProvider, Provider<WallpaperDao> wallpaperDaoProvider) {
    return new LauncherRepository_Factory(iconPositionDaoProvider, dockAppDaoProvider, widgetDaoProvider, hiddenAppDaoProvider, wallpaperDaoProvider);
  }

  public static LauncherRepository newInstance(IconPositionDao iconPositionDao,
      DockAppDao dockAppDao, WidgetDao widgetDao, HiddenAppDao hiddenAppDao,
      WallpaperDao wallpaperDao) {
    return new LauncherRepository(iconPositionDao, dockAppDao, widgetDao, hiddenAppDao, wallpaperDao);
  }
}
