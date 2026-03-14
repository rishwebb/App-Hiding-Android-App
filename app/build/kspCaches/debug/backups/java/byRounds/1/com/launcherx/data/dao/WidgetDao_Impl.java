package com.launcherx.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.launcherx.data.entities.WidgetEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WidgetDao_Impl implements WidgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WidgetEntity> __insertionAdapterOfWidgetEntity;

  private final EntityDeletionOrUpdateAdapter<WidgetEntity> __deletionAdapterOfWidgetEntity;

  private final EntityDeletionOrUpdateAdapter<WidgetEntity> __updateAdapterOfWidgetEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public WidgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWidgetEntity = new EntityInsertionAdapter<WidgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `widgets` (`id`,`page`,`row`,`col`,`widgetType`,`widgetSize`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WidgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPage());
        statement.bindLong(3, entity.getRow());
        statement.bindLong(4, entity.getCol());
        statement.bindString(5, entity.getWidgetType());
        statement.bindString(6, entity.getWidgetSize());
      }
    };
    this.__deletionAdapterOfWidgetEntity = new EntityDeletionOrUpdateAdapter<WidgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `widgets` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WidgetEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWidgetEntity = new EntityDeletionOrUpdateAdapter<WidgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `widgets` SET `id` = ?,`page` = ?,`row` = ?,`col` = ?,`widgetType` = ?,`widgetSize` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WidgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPage());
        statement.bindLong(3, entity.getRow());
        statement.bindLong(4, entity.getCol());
        statement.bindString(5, entity.getWidgetType());
        statement.bindString(6, entity.getWidgetSize());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM widgets WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM widgets";
        return _query;
      }
    };
  }

  @Override
  public Object insertWidget(final WidgetEntity widget,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWidgetEntity.insert(widget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWidget(final WidgetEntity widget,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWidgetEntity.handle(widget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWidget(final WidgetEntity widget,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWidgetEntity.handle(widget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final int id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WidgetEntity>> getAllWidgets() {
    final String _sql = "SELECT * FROM widgets ORDER BY page, row, col";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"widgets"}, new Callable<List<WidgetEntity>>() {
      @Override
      @NonNull
      public List<WidgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfRow = CursorUtil.getColumnIndexOrThrow(_cursor, "row");
          final int _cursorIndexOfCol = CursorUtil.getColumnIndexOrThrow(_cursor, "col");
          final int _cursorIndexOfWidgetType = CursorUtil.getColumnIndexOrThrow(_cursor, "widgetType");
          final int _cursorIndexOfWidgetSize = CursorUtil.getColumnIndexOrThrow(_cursor, "widgetSize");
          final List<WidgetEntity> _result = new ArrayList<WidgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WidgetEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpRow;
            _tmpRow = _cursor.getInt(_cursorIndexOfRow);
            final int _tmpCol;
            _tmpCol = _cursor.getInt(_cursorIndexOfCol);
            final String _tmpWidgetType;
            _tmpWidgetType = _cursor.getString(_cursorIndexOfWidgetType);
            final String _tmpWidgetSize;
            _tmpWidgetSize = _cursor.getString(_cursorIndexOfWidgetSize);
            _item = new WidgetEntity(_tmpId,_tmpPage,_tmpRow,_tmpCol,_tmpWidgetType,_tmpWidgetSize);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WidgetEntity>> getWidgetsForPage(final int page) {
    final String _sql = "SELECT * FROM widgets WHERE page = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, page);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"widgets"}, new Callable<List<WidgetEntity>>() {
      @Override
      @NonNull
      public List<WidgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfRow = CursorUtil.getColumnIndexOrThrow(_cursor, "row");
          final int _cursorIndexOfCol = CursorUtil.getColumnIndexOrThrow(_cursor, "col");
          final int _cursorIndexOfWidgetType = CursorUtil.getColumnIndexOrThrow(_cursor, "widgetType");
          final int _cursorIndexOfWidgetSize = CursorUtil.getColumnIndexOrThrow(_cursor, "widgetSize");
          final List<WidgetEntity> _result = new ArrayList<WidgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WidgetEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpRow;
            _tmpRow = _cursor.getInt(_cursorIndexOfRow);
            final int _tmpCol;
            _tmpCol = _cursor.getInt(_cursorIndexOfCol);
            final String _tmpWidgetType;
            _tmpWidgetType = _cursor.getString(_cursorIndexOfWidgetType);
            final String _tmpWidgetSize;
            _tmpWidgetSize = _cursor.getString(_cursorIndexOfWidgetSize);
            _item = new WidgetEntity(_tmpId,_tmpPage,_tmpRow,_tmpCol,_tmpWidgetType,_tmpWidgetSize);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
