package org.shufygoth.npcrl.plugin;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface used to handle any command arguments and sub-arguments.
 * You can use this interface recursively to handle sub-args of sub-args of sub-args if you wanted to.
 * It may be possible to cover all possibilities through recursion alone
 * <p>
 * It's up to you how you use this interface to handle command arguments.
 * You can use a Map<String, ArgHandler> and then populate the map with arguments and their handler instances.
 */
public interface ArgHandler {
    boolean handle(CommandSender sender, List<String> args);

    /**
     * By default, slices the head of the args list and forwards it to a sub-handler.
     * <p>
     * "Forward", meaning to execute the given arg handler's handle method with the first argument excluded.
     * <p>
     * This way, you can recursively handle all combinations of a long command with several arguments.
     * <p>
     * <p>
     * Example of method calls in-order for handling this command: /manager groups add Bob123
     * <p>
     * handle(sender, ["groups", "add", "Bob123"])
     * <p>
     * forward(sender, ["groups", "add", "Bob123"], subArgHandler)
     * <p>
     * ( in subArgHandler )
     * handle(sender, ["add", "Bob123"])
     * <p>
     * if args[0] == "add" ... add arg[1]
     * <p>
     * etc...
     * <p>
     * <p>
     * You can override this interface method if your command handler behaves in a different way.
     * @param sender The command sender to forward
     * @param args The arguments, excluding the first element, to forward to the arg handler
     * @param anotherArgHandler The arg handler to receive the sliced args list and command sender
     */
    default boolean forward(CommandSender sender, List<String> args, ArgHandler anotherArgHandler) {
        return anotherArgHandler.handle(sender, args.stream().skip(1).toList());
    }
}
