package me.cortex.voxy.client.mixin.minecraft.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.cortex.voxy.client.core.gl.Capabilities;
import com.mojang.renderpearl.backend.opengl.GlDebug;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.PrintWriter;
import java.io.StringWriter;

@Mixin(GlDebug.class)
public class MixinGlDebug {
    @WrapOperation(method = "printDebugLog", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private void voxy$wrapDebug(Logger instance, String base, Object msgObj, Operation<Void> original) {
        // Instead of casting to the private GlDebug.LogEntry, simply check its class name
        if (msgObj != null && msgObj.getClass().getSimpleName().equals("LogEntry")) {
            // Because LogEntry implements a custom toString() method, we can safely call it on the Object
            var throwable = new Throwable(msgObj.toString());
            if (isCausedByVoxy(throwable.getStackTrace())) {
                if (!isCausedByShaderCompileTest(throwable.getStackTrace())) {
                    original.call(instance, base + "\n" + getStackTraceAsString(throwable), throwable);
                }
            } else {
                original.call(instance, base, msgObj);
            }
        } else {
            original.call(instance, base, msgObj);
        }
    }

    @Unique
    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    @Unique
    private boolean isCausedByVoxy(StackTraceElement[] trace) {
        for (var elem : trace) {
            if (elem.getClassName().startsWith("me.cortex.voxy")) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean isCausedByShaderCompileTest(StackTraceElement[] trace) {
        for (var elem : trace) {
            if (elem.getClassName().equals(Capabilities.class.getName()) && elem.getMethodName().equals("testShaderCompilesOk")) {
                return true;
            }
        }
        return false;
    }
}
