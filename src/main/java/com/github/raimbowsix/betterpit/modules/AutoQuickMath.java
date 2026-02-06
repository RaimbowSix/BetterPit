package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class AutoQuickMath {
    public static void solveQuickMath(ClientChatReceivedEvent event){
        String rawMessage = event.message.getUnformattedText();
        if(ConfigOneConfig.quickMath && rawMessage.startsWith("QUICK MATHS! Solve: ") && Minecraft.getMinecraft().currentScreen==null){
            long millisStarted = System.currentTimeMillis();
            try{
                String mathProblem = rawMessage.replace("QUICK MATHS! Solve: ", "");
                new Thread(()->{
                    try {
                        int randomInt = (int) (Math.random() * 1000);
                        Thread.sleep(ConfigOneConfig.getQuickMathMinDelay+randomInt);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    long millisToSolve = System.currentTimeMillis() - millisStarted;
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText( "§7[§cBetterPit§7] §aSolved this math problem §7(" + millisToSolve + "ms)"));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("" + (int) eval(mathProblem.replace("x", "*")));
                }).start();
            }
            catch (Exception ignored){
            }
        }
    }

    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}