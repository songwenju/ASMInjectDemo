package com.xiaomi.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 自定义的AdviceAdapter
 * for
 */
public class CustomAdviceAdapter extends AdviceAdapter {
    protected CustomAdviceAdapter(MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(Opcodes.ASM5, methodVisitor, access, name, descriptor);
    }
}
