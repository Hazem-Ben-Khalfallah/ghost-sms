/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacknebula.ghostsms.model;

import android.database.Cursor;


import com.blacknebula.ghostsms.database.tables.Versions;

import java.util.Date;

/**
 * Represents an application version for this app
 * and an icon.
 */
public class Version extends CursorParser {
    /**
     * The application name.
     */
    public Integer version;

    public String application;

    public Long timestamp;

    public Version() {
    }

    public Version(Integer version, String application, Date date) {
        this.version = version;
        this.application = application;
        this.timestamp = date.getTime();
    }

    @Override
    public void populate(Cursor cursor) {
        this.version = cursor.getInt(cursor.getColumnIndexOrThrow(Versions._VERSION));
        this.application = cursor.getString(cursor.getColumnIndexOrThrow(Versions._APPLICATION));
        this.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Versions._TIMESTAMP));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;

        Version version1 = (Version) o;

        if (!version.equals(version1.version)) return false;
        return application.equals(version1.application);

    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + application.hashCode();
        return result;
    }
}
