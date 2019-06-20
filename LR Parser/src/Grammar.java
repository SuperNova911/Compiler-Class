import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grammar implements Serializable
{
    private String startSymbol;
    private final ArrayList<Rule> ruleList;
    private final HashSet<String> terminalSet;
    private final HashSet<String> nonTerminalSet;
    private HashMap<String, HashSet<String>> firstMap;
    private HashMap<String, HashSet<String>> followMap;

    public Grammar(String grammarText)
    {
        ruleList = new ArrayList<>();
        terminalSet = new HashSet<>();
        nonTerminalSet = new HashSet<>();

        StringTokenizer tokenizer = new StringTokenizer(grammarText, " ");
        String token, previousToken;

        String leftSymbol = null;
        ArrayList<String> rightSymbolList = new ArrayList<>();

        token = null;
        while (tokenizer.hasMoreTokens())
        {
            previousToken = token;
            token = tokenizer.nextToken();

            if (previousToken == null)
            {
                continue;
            }

            if (token.equals("==>"))
            {
                if (rightSymbolList.isEmpty())
                {
                    leftSymbol = previousToken;
                    nonTerminalSet.add(previousToken);
                    token = null;
                    continue;
                }

                if (startSymbol == null)
                {
                    startSymbol = leftSymbol;
                    ruleList.add(new Rule("S'", new String[]{leftSymbol}));
                }

                ruleList.add(new Rule(leftSymbol, rightSymbolList.toArray(new String[rightSymbolList.size()])));
                leftSymbol = previousToken;
                nonTerminalSet.add(previousToken);
                rightSymbolList.clear();
                token = null;
            }
            else if (token.equals("||"))
            {
                rightSymbolList.add(previousToken);
                terminalSet.add(previousToken);
                ruleList.add(new Rule(leftSymbol, rightSymbolList.toArray(new String[rightSymbolList.size()])));
                rightSymbolList.clear();
                token = null;
            }
            else
            {
                rightSymbolList.add(previousToken);
                terminalSet.add(previousToken);
            }
        }

        rightSymbolList.add(token);
        terminalSet.add(token);
        ruleList.add(new Rule(leftSymbol, rightSymbolList.toArray(new String[rightSymbolList.size()])));

        terminalSet.removeAll(nonTerminalSet);

        GenerateFirstMap();
        GenerateFollowMap();
    }

    public String PrintFirstSet()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("====== FIRST ======\n");
        for (String nonTerminalSymbol : nonTerminalSet)
        {
            HashSet<String> item = followMap.get(nonTerminalSymbol);
            stringBuilder.append(String.format("FIRST(%s) = %s\n", nonTerminalSymbol, item == null ? "" : item));
        }

        return stringBuilder.toString();
    }

    public String PrintFollowSet()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("====== FOLLOW ======\n");
        for (String nonTerminalSymbol : nonTerminalSet)
        {
            HashSet<String> item = followMap.get(nonTerminalSymbol);
            stringBuilder.append(String.format("FOLLOW(%s) = %s\n", nonTerminalSymbol, item == null ? "" : item));
        }

        return stringBuilder.toString();
    }

    public int FindRuleIndex(Rule rule)
    {
        return IntStream.range(0, ruleList.size()).filter(index -> ruleList.get(index).equals(rule)).findFirst().orElse(-1);
    }

    public HashSet<String> FindFirst(String[] symbolArray, int index)
    {
        HashSet<String> foundFirstSet = new HashSet<>();

        if (index == symbolArray.length)
        {
            return foundFirstSet;
        }

        if (terminalSet.contains(symbolArray[index]))
        {
            foundFirstSet.add(symbolArray[index]);
            return foundFirstSet;
        }

        if (nonTerminalSet.contains(symbolArray[index]))
        {
            foundFirstSet.addAll(firstMap.get(symbolArray[index]));
        }

        if (foundFirstSet.contains("__") && index != symbolArray.length - 1)
        {
            foundFirstSet.addAll(FindFirst(symbolArray, index + 1));
            foundFirstSet.remove("__");
        }

        return foundFirstSet;
    }

    public HashSet<Rule> GetRuleSetByLeftSymbol(String leftSymbol)
    {
        return ruleList.stream().filter(rule -> rule.GetLeftSymbol().equals(leftSymbol)).collect(Collectors.toCollection(HashSet::new));
    }

    public boolean IsNonTerminal(String symbol)
    {
        return nonTerminalSet.contains(symbol);
    }

    private void GenerateFirstMap()
    {
        firstMap = new HashMap<>();

        for (String nonTerminalSymbol : nonTerminalSet)
        {
            firstMap.put(nonTerminalSymbol, new HashSet<>());
        }

        boolean hasUpdated = true;
        while (hasUpdated)
        {
            hasUpdated = false;

            for (String nonTerminalSymbol : nonTerminalSet)
            {
                HashSet<String> foundFirstSet = new HashSet<>();
                for (Rule rule : ruleList)
                {
                    if (rule.GetLeftSymbol().equals(nonTerminalSymbol))
                    {
                        foundFirstSet.addAll(FindFirst(rule.GetRightSymbolArray(), 0));
                    }
                }
                if (this.firstMap.get(nonTerminalSymbol).containsAll(foundFirstSet) == false)
                {
                    hasUpdated = true;
                    this.firstMap.get(nonTerminalSymbol).addAll(foundFirstSet);
                }
            }
        }

        firstMap.put("S'", firstMap.get(startSymbol));
    }

    private void GenerateFollowMap()
    {
        followMap = new HashMap<>();

        for (String nonTerminalSymbol : nonTerminalSet)
        {
            followMap.put(nonTerminalSymbol, new HashSet<>());
        }

        followMap.put("S'", new HashSet<>(Collections.singletonList("$")));

        boolean hasUpdated = true;
        while (hasUpdated)
        {
            hasUpdated = false;

            for (String nonTerminalSymbol : nonTerminalSet)
            {
                for (Rule rule : ruleList)
                {
                    for (int index = 0; index < rule.GetRightSymbolArray().length; index++)
                    {
                        String[] rightSymbolArray = rule.GetRightSymbolArray();
                        if (rightSymbolArray[index].equals(nonTerminalSymbol))
                        {
                            if (index == rightSymbolArray.length - 1)
                            {
                                followMap.get(nonTerminalSymbol).addAll(followMap.get(rule.leftSymbol));
                            }
                            else
                            {
                                HashSet<String> foundFirstSet = FindFirst(rightSymbolArray, index + 1);

                                if (foundFirstSet.contains("__"))
                                {
                                    foundFirstSet.remove("__");
                                    foundFirstSet.addAll(followMap.get(rule.leftSymbol));
                                }
                                if (followMap.get(nonTerminalSymbol).containsAll(foundFirstSet) == false)
                                {
                                    followMap.get(nonTerminalSymbol).addAll(foundFirstSet);
                                    hasUpdated = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Rule> GetRuleList()
    {
        return ruleList;
    }

    public HashSet<String> GetTerminalSet()
    {
        return terminalSet;
    }

    public HashSet<String> GetNonTerminalSet()
    {
        return nonTerminalSet;
    }

    public HashMap<String, HashSet<String>> GetFirstMap()
    {
        return firstMap;
    }

    public HashMap<String, HashSet<String>> GetFollowMap()
    {
        return followMap;
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
        Grammar grammar = (Grammar) o;
        return startSymbol.equals(grammar.startSymbol) && ruleList.equals(grammar.ruleList) && terminalSet.equals(grammar.terminalSet) && nonTerminalSet.equals(
                grammar.nonTerminalSet) && Objects.equals(firstMap, grammar.firstMap) && Objects.equals(followMap, grammar.followMap);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(startSymbol, ruleList, terminalSet, nonTerminalSet, firstMap, followMap);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Rule rule : ruleList)
        {
            stringBuilder.append(rule).append("\n");
        }
        return stringBuilder.toString();
    }
}
