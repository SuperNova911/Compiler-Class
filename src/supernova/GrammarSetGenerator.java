package supernova;

import java.util.StringTokenizer;

public class GrammarSetGenerator
{
    public static GrammarSet ParseGrammar(String input)
    {
        GrammarSet grammarSet = new GrammarSet();

        Grammar grammar = null;
        SymbolSet symbolSet = new SymbolSet();

        StringTokenizer stringTokenizer = new StringTokenizer(input, Grammar.DELIM_STRING);
        String token = null;
        String previousToken = null;

        while (stringTokenizer.hasMoreTokens())
        {
            previousToken = token;
            token = stringTokenizer.nextToken();

            if (previousToken == null)
            {
                continue;
            }

            if (token.equals(Grammar.DERIVATION_STRING))
            {
                if (IsTerminal(previousToken))
                {
                    System.out.println("잘못된 문법입니다.");
                    break;
                }

                if (grammar == null)
                {
                    grammar = new Grammar(new Symbol(previousToken, Symbol.SymbolType.NonTerminal));
                    continue;
                }

                if (symbolSet.IsSetEmpty() == false)
                {
                    grammar.AddRightSymbolSet(symbolSet);
                    symbolSet = new SymbolSet();
                }

                grammarSet.AddGrammar(grammar);
                grammar = new Grammar(new Symbol(previousToken, Symbol.SymbolType.NonTerminal));
            }
            else if (token.equals(Grammar.OR_STRING))
            {
                if (grammar == null)
                {
                    System.out.println("심볼을 넣을 문법이 없습니다.");
                    break;
                }

                if (IsSymbol(previousToken) == false)
                {
                    System.out.println("잘못된 문법입니다, 심볼이 없습니다.");
                    break;
                }

                symbolSet.AppendSymbol(new Symbol(previousToken, IsTerminal(previousToken) ? Symbol.SymbolType.Terminal : Symbol.SymbolType.NonTerminal));
                grammar.AddRightSymbolSet(symbolSet);
                symbolSet = new SymbolSet();
            }
            else
            {
                if (previousToken.equals(Grammar.EPSILON_STRING))
                {
                    symbolSet.AppendSymbol(new Epsilon());
                }
                else if (IsSymbol(previousToken))
                {
                    symbolSet.AppendSymbol(new Symbol(previousToken, IsTerminal(previousToken) ? Symbol.SymbolType.Terminal : Symbol.SymbolType.NonTerminal));
                }
            }
        }

        if (grammar != null && IsSymbol(token))
        {
            symbolSet.AppendSymbol(new Symbol(token, IsTerminal(token) ? Symbol.SymbolType.Terminal : Symbol.SymbolType.NonTerminal));
            grammar.AddRightSymbolSet(symbolSet);
            grammarSet.AddGrammar(grammar);
        }

        return grammarSet;
    }

    private static boolean IsSymbol(String token)
    {
        if (token == null)
        {
            return false;
        }

        return !token.equals(Grammar.DERIVATION_STRING) && !token.equals(Grammar.EPSILON_STRING) && !token.equals(Grammar.OR_STRING);
    }

    private static boolean IsTerminal(String token)
    {
        if (token == null)
        {
            return false;
        }

        if (IsSymbol(token) == false)
        {
            return false;
        }

        return !Character.isUpperCase(token.charAt(0));
    }
}