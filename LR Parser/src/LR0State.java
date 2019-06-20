import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class LR0State implements Serializable
{
    private final LinkedHashSet<LR0Item> lr0ItemSet;
    private final HashMap<String, LR0State> transitionMap;

    public LR0State(Grammar grammar, HashSet<LR0Item> lr0ItemSet)
    {
        this.lr0ItemSet = new LinkedHashSet<>(lr0ItemSet);
        this.transitionMap = new HashMap<>();

        GenerateClosure(grammar);
    }

    private void GenerateClosure(Grammar grammar)
    {
        boolean hasUpdated = true;
        while (hasUpdated)
        {
            hasUpdated = false;

            HashSet<LR0Item> generatedItemSet = new HashSet<>();
            for (LR0Item lr0Item : lr0ItemSet)
            {
                if (lr0Item.GetMarkSymbol() != null && grammar.IsNonTerminal(lr0Item.GetMarkSymbol()))
                {
                    HashSet<Rule> matchRuleSet = grammar.GetRuleSetByLeftSymbol(lr0Item.GetMarkSymbol());
                    generatedItemSet.addAll(GenerateLR0Item(matchRuleSet));
                }
            }

            if (lr0ItemSet.containsAll(generatedItemSet) == false)
            {
                lr0ItemSet.addAll(generatedItemSet);
                hasUpdated = true;
            }
        }
    }

    private HashSet<LR0Item> GenerateLR0Item(HashSet<Rule> ruleSet)
    {
        return ruleSet.stream().map(LR0Item::new).collect(Collectors.toCollection(HashSet::new));
    }

    public void AddTransition(String symbol, LR0State lr0State)
    {
        transitionMap.put(symbol, lr0State);
    }

    public HashSet<LR0Item> GetLR0ItemSet()
    {
        return lr0ItemSet;
    }

    public HashMap<String, LR0State> GetTransitionMap()
    {
        return transitionMap;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        LR0State lr0State = (LR0State) o;
        return lr0ItemSet.equals(lr0State.lr0ItemSet) && transitionMap.equals(lr0State.transitionMap);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(lr0ItemSet, transitionMap);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (LR0Item item : lr0ItemSet)
        {
            stringBuilder.append(String.format("[%s]", item));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}