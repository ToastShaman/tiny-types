package com.github.toastshaman.tinytypes.events.format.jackson;

import com.github.toastshaman.tinytypes.events.Event;

record MyEvent(Integer id, String name) implements Event {}
