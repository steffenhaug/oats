package io.github.steffenhaug.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractRailBlock.class)
public abstract class AbstractRailBlockMixin {
	// Redirects the rails check for top rims to a method that does the exact same
	// calculation EXCEPT that it ignores honey blocks missing top rims, so rails
	// can be placed on honey.

	// It's quite crude, but it should hopefully not do any severe damage in terms
	// of compatibility, because if the block checked is _not_ a honey block, I
	// just return the real value of the function as is, so the behaviour in 99%
	// of cases should be exactly identical even if a mod has added mixins to the
	// real hasTopRim function.

	// One is static and the other is not, so we have to do two different redirects for the two
	// contexts the function is used.
	@Redirect(method = "shouldDropRail",
		at = @At(value="INVOKE", target="net/minecraft/block/AbstractRailBlock.hasTopRim (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private static boolean shouldDropRail_hasTopRim_intercept(BlockView world, BlockPos pos) {
		return hasTopRim(world, pos);
	}

	@Redirect(method = "canPlaceAt",
			at = @At(value="INVOKE", target="net/minecraft/block/AbstractRailBlock.hasTopRim (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean canPlaceAt_hasTopRim_intercept(BlockView world, BlockPos pos) {
		return hasTopRim(world, pos);
	}

	private static boolean hasTopRim(BlockView world, BlockPos pos) {
		BlockState bs = world.getBlockState(pos);

		if (bs.getBlock() instanceof HoneyBlock) {
			return true;
		}

		return AbstractRailBlock.hasTopRim(world, pos);
	}


}
