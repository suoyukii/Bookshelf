package net.darkhax.bookshelf.asm;

import net.darkhax.bookshelf.asm.transformers.TransformerEnchantmentHelper;
import net.minecraft.launchwrapper.IClassTransformer;

public class BookshelfTransformerManager implements IClassTransformer {

    @Override
    public byte[] transform (String name, String transformedName, byte[] classBytes) {

        if ("net.minecraft.enchantment.EnchantmentHelper".equals(name))
            return TransformerEnchantmentHelper.transform(name, transformedName, classBytes);

        return classBytes;
    }
}