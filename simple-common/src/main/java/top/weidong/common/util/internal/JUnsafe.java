package top.weidong.common.util.internal;
/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import sun.misc.Unsafe;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static top.weidong.common.util.StackTraceUtil.stackTrace;


/**
 * For the {@link Unsafe} access.
 *
 * jupiter
 * org.jupiter.common.util.internal
 *
 * @author jiachun.fjc
 */
public final class JUnsafe {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(JUnsafe.class);

    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("sun.misc.Unsafe.theUnsafe: unavailable, {}.", stackTrace(t));
            }

            unsafe = null;
        }

        UNSAFE = unsafe;
    }

    /**
     * Returns the {@link Unsafe}'s instance.
     */
    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    /**
     * Returns the system {@link ClassLoader}.
     */
    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

                @Override
                public ClassLoader run() {
                    return ClassLoader.getSystemClassLoader();
                }
            });
        }
    }

    private JUnsafe() {}
}
