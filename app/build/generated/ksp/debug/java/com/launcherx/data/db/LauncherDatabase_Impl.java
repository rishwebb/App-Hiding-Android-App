package com.launcherx.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.launcherx.data.dao.DockAppDao;
import com.launcherx.data.dao.DockAppDao_Impl;
import com.launcherx.data.dao.HiddenAppDao;
import com.launcherx.data.dao.HiddenAppDao_Impl;
import com.launcherx.data.dao.IconPositionDao;
import com.launcherx.data.dao.IconPositionDao_Impl;
import com.launcherx.data.dao.WallpaperDao;
import com.launcherx.data.dao.WallpaperDao_Impl;
import com.launcherx.data.dao.WidgetDao;
import com.launcherx.data.dao.WidgetDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LauncherDatabase_Impl extends LauncherDatabase {
  private volatile IconPositionDao _iconPositionDao;

  private volatile DockAppDao _dockAppDao;

  private volatile WidgetDao _widgetDao;

  private volatile HiddenAppDao _hiddenAppDao;

  private volatile WallpaperDao _wallpaperDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `icon_positions` (`packageName` TEXT NOT NULL, `page` INTEGER NOT NULL, `row` INTEGER NOT NULL, `col` INTEGER NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dock_apps` (`slot` INTEGER NOT NULL, `packageName` TEXT NOT NULL, PRIMARY KEY(`slot`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `widgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `page` INTEGER NOT NULL, `row` INTEGER NOT NULL, `col` INTEGER NOT NULL, `widgetType` TEXT NOT NULL, `widgetSize` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `hidden_apps` (`packageName` TEXT NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `wallpaper` (`id` INTEGER NOT NULL, `uri` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '484655a096dc32c527ddc6a79437036a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `icon_positions`");
        db.execSQL("DROP TABLE IF EXISTS `dock_apps`");
        db.execSQL("DROP TABLE IF EXISTS `widgets`");
        db.execSQL("DROP TABLE IF EXISTS `hidden_apps`");
        db.execSQL("DROP TABLE IF EXISTS `wallpaper`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsIconPositions = new HashMap<String, TableInfo.Column>(4);
        _columnsIconPositions.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIconPositions.put("page", new TableInfo.Column("page", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIconPositions.put("row", new TableInfo.Column("row", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIconPositions.put("col", new TableInfo.Column("col", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIconPositions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIconPositions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIconPositions = new TableInfo("icon_positions", _columnsIconPositions, _foreignKeysIconPositions, _indicesIconPositions);
        final TableInfo _existingIconPositions = TableInfo.read(db, "icon_positions");
        if (!_infoIconPositions.equals(_existingIconPositions)) {
          return new RoomOpenHelper.ValidationResult(false, "icon_positions(com.launcherx.data.entities.IconPositionEntity).\n"
                  + " Expected:\n" + _infoIconPositions + "\n"
                  + " Found:\n" + _existingIconPositions);
        }
        final HashMap<String, TableInfo.Column> _columnsDockApps = new HashMap<String, TableInfo.Column>(2);
        _columnsDockApps.put("slot", new TableInfo.Column("slot", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDockApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDockApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDockApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDockApps = new TableInfo("dock_apps", _columnsDockApps, _foreignKeysDockApps, _indicesDockApps);
        final TableInfo _existingDockApps = TableInfo.read(db, "dock_apps");
        if (!_infoDockApps.equals(_existingDockApps)) {
          return new RoomOpenHelper.ValidationResult(false, "dock_apps(com.launcherx.data.entities.DockAppEntity).\n"
                  + " Expected:\n" + _infoDockApps + "\n"
                  + " Found:\n" + _existingDockApps);
        }
        final HashMap<String, TableInfo.Column> _columnsWidgets = new HashMap<String, TableInfo.Column>(6);
        _columnsWidgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWidgets.put("page", new TableInfo.Column("page", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWidgets.put("row", new TableInfo.Column("row", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWidgets.put("col", new TableInfo.Column("col", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWidgets.put("widgetType", new TableInfo.Column("widgetType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWidgets.put("widgetSize", new TableInfo.Column("widgetSize", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWidgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWidgets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWidgets = new TableInfo("widgets", _columnsWidgets, _foreignKeysWidgets, _indicesWidgets);
        final TableInfo _existingWidgets = TableInfo.read(db, "widgets");
        if (!_infoWidgets.equals(_existingWidgets)) {
          return new RoomOpenHelper.ValidationResult(false, "widgets(com.launcherx.data.entities.WidgetEntity).\n"
                  + " Expected:\n" + _infoWidgets + "\n"
                  + " Found:\n" + _existingWidgets);
        }
        final HashMap<String, TableInfo.Column> _columnsHiddenApps = new HashMap<String, TableInfo.Column>(1);
        _columnsHiddenApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHiddenApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHiddenApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHiddenApps = new TableInfo("hidden_apps", _columnsHiddenApps, _foreignKeysHiddenApps, _indicesHiddenApps);
        final TableInfo _existingHiddenApps = TableInfo.read(db, "hidden_apps");
        if (!_infoHiddenApps.equals(_existingHiddenApps)) {
          return new RoomOpenHelper.ValidationResult(false, "hidden_apps(com.launcherx.data.entities.HiddenAppEntity).\n"
                  + " Expected:\n" + _infoHiddenApps + "\n"
                  + " Found:\n" + _existingHiddenApps);
        }
        final HashMap<String, TableInfo.Column> _columnsWallpaper = new HashMap<String, TableInfo.Column>(2);
        _columnsWallpaper.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWallpaper.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWallpaper = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWallpaper = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWallpaper = new TableInfo("wallpaper", _columnsWallpaper, _foreignKeysWallpaper, _indicesWallpaper);
        final TableInfo _existingWallpaper = TableInfo.read(db, "wallpaper");
        if (!_infoWallpaper.equals(_existingWallpaper)) {
          return new RoomOpenHelper.ValidationResult(false, "wallpaper(com.launcherx.data.entities.WallpaperEntity).\n"
                  + " Expected:\n" + _infoWallpaper + "\n"
                  + " Found:\n" + _existingWallpaper);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "484655a096dc32c527ddc6a79437036a", "0248b91b9d57124433f415b6c8c611b4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "icon_positions","dock_apps","widgets","hidden_apps","wallpaper");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `icon_positions`");
      _db.execSQL("DELETE FROM `dock_apps`");
      _db.execSQL("DELETE FROM `widgets`");
      _db.execSQL("DELETE FROM `hidden_apps`");
      _db.execSQL("DELETE FROM `wallpaper`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(IconPositionDao.class, IconPositionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DockAppDao.class, DockAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WidgetDao.class, WidgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HiddenAppDao.class, HiddenAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WallpaperDao.class, WallpaperDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public IconPositionDao iconPositionDao() {
    if (_iconPositionDao != null) {
      return _iconPositionDao;
    } else {
      synchronized(this) {
        if(_iconPositionDao == null) {
          _iconPositionDao = new IconPositionDao_Impl(this);
        }
        return _iconPositionDao;
      }
    }
  }

  @Override
  public DockAppDao dockAppDao() {
    if (_dockAppDao != null) {
      return _dockAppDao;
    } else {
      synchronized(this) {
        if(_dockAppDao == null) {
          _dockAppDao = new DockAppDao_Impl(this);
        }
        return _dockAppDao;
      }
    }
  }

  @Override
  public WidgetDao widgetDao() {
    if (_widgetDao != null) {
      return _widgetDao;
    } else {
      synchronized(this) {
        if(_widgetDao == null) {
          _widgetDao = new WidgetDao_Impl(this);
        }
        return _widgetDao;
      }
    }
  }

  @Override
  public HiddenAppDao hiddenAppDao() {
    if (_hiddenAppDao != null) {
      return _hiddenAppDao;
    } else {
      synchronized(this) {
        if(_hiddenAppDao == null) {
          _hiddenAppDao = new HiddenAppDao_Impl(this);
        }
        return _hiddenAppDao;
      }
    }
  }

  @Override
  public WallpaperDao wallpaperDao() {
    if (_wallpaperDao != null) {
      return _wallpaperDao;
    } else {
      synchronized(this) {
        if(_wallpaperDao == null) {
          _wallpaperDao = new WallpaperDao_Impl(this);
        }
        return _wallpaperDao;
      }
    }
  }
}
