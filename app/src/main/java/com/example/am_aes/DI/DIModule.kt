package com.example.am_aes.DI

import android.content.Context

import com.example.am_aes.Database.MessageDao
import com.example.am_aes.Database.MessageDatabase
import com.example.am_aes.Repository.MessageRepository
import com.example.am_aes.Repository.RoomMessageRepository
import com.example.am_aes.Repository.SharedPrefsMessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun provideMessageDao(@ApplicationContext context: Context): MessageDao {
        return MessageDatabase.getInstance(context).messageDao()
    }

    @Provides
    @Named("room")
    fun provideRoomRepository(messageDao: MessageDao): MessageRepository {
        return RoomMessageRepository(messageDao)
    }

    @Provides
    @Named("sharedPrefs")
    fun provideSharedPrefsRepository(@ApplicationContext context: Context): MessageRepository {
        return SharedPrefsMessageRepository(context)
    }
}
