package com.pyra.krpytapplication.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2
import com.pyra.krpytapplication.roomDb.dao.*
import com.pyra.krpytapplication.roomDb.entity.*


@Database(
    version = 2,
    entities = [ChatListSchema::class, ChatMessagesSchema::class, GroupParticipationSchema::class, BlockListSchema::class, BurnMessageSchema::class]
)
abstract class AppDataBase : RoomDatabase() {

    companion object {
        private var instance: AppDataBase? = null
        private val DATABASENAME = "KryptDb"


        fun getInstance(context: Context): AppDataBase? {
            if (instance == null) {
                synchronized(AppDataBase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java,
                            DATABASENAME
                        ).addMigrations(MIGRATION_1_2)
                            .build()
                    }
                }
            }
            return instance
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE ChatList " +
                            "ADD 'isOnline' INTEGER default 0 NOT NULL"
                )

                database.execSQL(
                    "ALTER TABLE ChatList " +
                            "ADD 'lastSeen' TEXT default '' NOT NULL"
                )
            }
        }

    }


    abstract fun chatListDao(): ChatListDao?
    abstract fun chatMessagesDao(): ChatMessagesDao?
    abstract fun clearDbDao(): ClearDbDao?
    abstract fun groupPaticipationDao(): GroupParticipationDao?
    abstract fun blockListDao(): BlockListDao?
    abstract fun burnMessageDao(): BurnMessageDao?

}