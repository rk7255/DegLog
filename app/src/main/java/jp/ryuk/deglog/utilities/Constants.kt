package jp.ryuk.deglog.utilities

const val DIARY_TABLE = "diary_table"
const val PROFILE_TABLE = "profile_table"
const val TODO_TABLE = "todo_table"
const val DATABASE_NAME = "app_database"

// Shared Preferences
const val SHARED_PREF_KEY = "shared_pref"
const val KEY_DASHBOARD = "dashboard"
const val KEY_CHECKED_NAME = "checked_name"
const val KEY_CHECKED_CHART = "checked_chart"
const val KEY_CHECKED_AXIS = "checked_axis"
const val KEY_UNIT_WEIGHT = "unit_weight"
const val KEY_UNIT_LENGTH = "unit_length"
const val KEY_DEATH_DAY = "death_day"
const val KEY_DB1_ENABLED = "db1_enabled"
const val KEY_DB1_NAME = "db1_name"
const val KEY_DB1_UNIT = "db1_unit"
const val KEY_DB2_ENABLED = "db2_enabled"
const val KEY_DB2_NAME = "db2_name"
const val KEY_DB2_UNIT = "db2_unit"

const val deg = "DEGLOG_DEBUG"
const val TAG = "DEGLOG_DEBUG"

class MessageCode {
    companion object {
        const val COLLECT = 0
        const val NAME_EMPTY = 1
        const val NAME_UNREGISTERED = 2
        const val NAME_REGISTERED = 3
        const val NUMBER_EMPTY = 4
        const val EDIT = 5
    }
}

class WhereClicked {
    companion object {
        const val UNKNOWN = 0
        const val ICON = 1
        const val NAME = 2
        const val NOTIFY = 3
        const val WEIGHT = 4
        const val LENGTH = 5
        const val TODO = 6
        const val FREE1 = 7
        const val FREE2 = 8
    }
}

class NavMode {
    companion object {
        const val NEW = 0
        const val EDIT = 1
        const val DASHBOARD = 2

        const val FROM_WEIGHT = 0
        const val FROM_LENGTH = 1
    }
}

class Deco {
    companion object {
        const val DASHBOARD = 0
        const val CHART = 1
    }
}