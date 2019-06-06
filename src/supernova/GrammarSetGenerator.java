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
        String token, previousToken;

        token = null;
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
                if (Symbol.FindSymbolType(previousToken) != Symbol.SymbolType.NonTerminal)
                {
                    System.out.println("잘못된 문법입니다. 유도 심볼 이전에는 터미널 심볼이 나타나야 합니다");
                    break;
                }

                if (grammar == null)
                {
                    grammar = new Grammar(new Symbol(previousToken));
                    continue;
                }

                grammar.AddRightSymbolSet(symbolSet);
                symbolSet = new SymbolSet();

                grammarSet.AddGrammar(grammar);
                grammar = new Grammar(new Symbol(previousToken));
            }
            else if (token.equals(Grammar.OR_STRING))
            {
                if (grammar == null)
                {
                    System.out.println("심볼을 넣을 문법이 없습니다.");
                    break;
                }

                if (Symbol.IsSymbol(previousToken) == false && Symbol.FindSymbolType(previousToken) != Symbol.SymbolType.Epsilon)
                {
                    System.out.println("잘못된 문법입니다, 문법에 추가 할 심볼이 없습니다.");
                    break;
                }

                symbolSet.AppendSymbol(new Symbol(previousToken));
                grammar.AddRightSymbolSet(symbolSet);
                symbolSet = new SymbolSet();
            }
            else if (Symbol.IsSymbol(previousToken))
            {
                symbolSet.AppendSymbol(new Symbol(previousToken));
            }
        }

        if (grammar != null)
        {
            if (Symbol.IsSymbol(token))
            {
                symbolSet.AppendSymbol(new Symbol(token));
            }
            grammar.AddRightSymbolSet(symbolSet);
            grammarSet.AddGrammar(grammar);
        }

        return grammarSet;
    }
}