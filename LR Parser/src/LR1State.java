import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class LR1State implements Serializable
{
    private final LinkedHashSet<LR1Item> lr1ItemSet;
    private final HashMap<String, LR1State> transitionMap;

    public LR1State(Grammar grammar, HashSet<LR1Item> lr1ItemSet)
    {
        this.lr1ItemSet = new LinkedHashSet<>(lr1ItemSet);
        transitionMap = new HashMap<>();

        GenerateClosure(grammar);
    }

    private void GenerateClosure(Grammar grammar)
    {
        boolean hasUpdated = true;

        while (hasUpdated)
        {
            hasUpdated = false;

            HashSet<LR1Item> generatedItemSet = new HashSet<>();
            for (LR1Item lr1Item : lr1ItemSet)
            {
                if (lr1Item.GetDotSymbolPosition() != lr1Item.GetRightSymbolArray().length && grammar.IsNonTerminal(lr1Item.GetMarkSymbol()))
                {
                    HashSet<String> lookAhead = new HashSet<>();
                    if (lr1Item.GetDotSymbolPosition() == lr1Item.GetRightSymbolArray().length - 1)
                    {
                        lookAhead.addAll(lr1Item.GetLookAheadSet());
                    }
                    else
                    {
                        HashSet<String> firstSet = grammar.FindFirst(lr1Item.GetRightSymbolArray(), lr1Item.GetDotSymbolPosition() + 1);
                        if (firstSet.contains("__"))
                        {
                            firstSet.remove("__");
                            firstSet.addAll(lr1Item.GetLookAheadSet());
                        }
                        lookAhead.addAll(firstSet);
                    }

                    for (Rule rule : grammar.GetRuleSetByLeftSymbol(lr1Item.GetMarkSymbol()))
                    {
                        generatedItemSet.add(new LR1Item(rule.GetLeftSymbol(), rule.GetRightSymbolArray(), 0, lookAhead));
                    }
                }
            }

            if (lr1ItemSet.containsAll(generatedItemSet) == false)
            {
                lr1ItemSet.addAll(generatedItemSet);
                hasUpdated = true;
            }
        }
    }

    public HashMap<String, LR1State> GetTransitionMap()
    {
        return transitionMap;
    }

    public LinkedHashSet<LR1Item> GetLR1ItemSet()
    {
        return lr1ItemSet;
    }

    @Override
    public String toString()
    {
        HashMap<LR0Item, HashSet<String>> combinedSet = new HashMap<>();
        for (LR1Item lr1Item : lr1ItemSet)
        {
            var target = new LR0Item(lr1Item.GetLeftSymbol(), lr1Item.GetRightSymbolArray(), lr1Item.GetDotSymbolPosition());
            if (combinedSet.containsKey(target))
            {
                combinedSet.get(target).addAll(lr1Item.GetLookAheadSet());
            }
            else
            {
                combinedSet.put(target, lr1Item.GetLookAheadSet());
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (LR0Item lr1Item : combinedSet.keySet())
        {
            stringBuilder.append(String.format("[%s, ", lr1Item));

            for (var lookAhead : combinedSet.get(lr1Item))
            {
                stringBuilder.append(String.format("%s/", lookAhead));
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]");
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}