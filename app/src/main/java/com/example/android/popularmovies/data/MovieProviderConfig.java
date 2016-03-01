package com.example.android.popularmovies.data;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by hania on 24.02.16.
 */

@SimpleSQLConfig(
        name = "MovieProvider",
        authority = "com.example.android.popularmovies",
        database = "movies.db",
        version = 1)

public class MovieProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
