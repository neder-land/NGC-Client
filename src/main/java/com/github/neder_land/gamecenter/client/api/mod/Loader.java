package com.github.neder_land.gamecenter.client.api.mod;

import com.github.neder_land.gamecenter.client.api.Client;

import java.io.IOException;
import java.util.List;

public interface Loader<T extends IModInfo> {
    List<T> detect(Client client) throws IOException;

    void verifyModCompatibility(List<T> list);

    ModEnv buildModEnvironment(List<T> unique);
}
