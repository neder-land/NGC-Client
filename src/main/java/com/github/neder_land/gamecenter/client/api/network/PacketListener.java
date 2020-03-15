package com.github.neder_land.gamecenter.client.api.network;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface PacketListener {
}
