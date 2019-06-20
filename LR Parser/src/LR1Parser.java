import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.IntStream;

public class LR1Parser extends LRParser
{
    private ArrayList<LR1State> canonicalCollection;

    public LR1Parser(Grammar grammar)
    {
        super(grammar);
    }

    public boolean TryParseCLR()
    {
        GenerateStates_CLR();
        GenerateGoToTable();

        return GenerateActionTable();
    }

    public boolean TryParseLALR()
    {
        GenerateState_LALR();
        GenerateGoToTable();

        return GenerateActionTable();
    }

    public String PrintCanonicalCollection()
    {
        StringBuilder stringBuilder = new StringBuilder("====== 정규 항목 집합 ======\n");
        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            stringBuilder.append(String.format("상태 %d\n", index));
            stringBuilder.append(canonicalCollection.get(index));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void GenerateStates_CLR()
    {
        canonicalCollection = new ArrayList<>();

        HashSet<LR1Item> startItem = new HashSet<>();
        Rule startRule = grammar.GetRuleList().get(0);
        HashSet<String> startLockAhead = new HashSet<>();
        startLockAhead.add("$");
        startItem.add(new LR1Item(startRule.GetLeftSymbol(), startRule.GetRightSymbolArray(), 0, startLockAhead));

        canonicalCollection.add(new LR1State(grammar, startItem));

        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            HashSet<String> markSymbolSet = new HashSet<>();
            for (LR1Item lr1Item : canonicalCollection.get(index).GetLR1ItemSet())
            {
                String markSymbol = lr1Item.GetMarkSymbol();
                if (markSymbol != null)
                {
                    markSymbolSet.add(markSymbol);
                }
            }

            for (String markSymbol : markSymbolSet)
            {
                HashSet<LR1Item> nextStateItemSet = new HashSet<>();
                for (LR1Item lr1Item : canonicalCollection.get(index).GetLR1ItemSet())
                {
                    if (lr1Item.GetMarkSymbol() != null && lr1Item.GetMarkSymbol().equals(markSymbol))
                    {
                        LR1Item shiftedItem = new LR1Item(lr1Item);
                        shiftedItem.ShiftDotPosition();
                        nextStateItemSet.add(shiftedItem);
                    }
                }

                LR1State nextState = new LR1State(grammar, nextStateItemSet);

                boolean isSame = false;
                for (LR1State lr1State : canonicalCollection)
                {
                    LinkedHashSet<LR1Item> lr1ItemSet = lr1State.GetLR1ItemSet();
                    LinkedHashSet<LR1Item> nextStateLR1ItemSet = nextState.GetLR1ItemSet();

                    if (lr1ItemSet.containsAll(nextStateItemSet) && nextStateLR1ItemSet.containsAll(lr1ItemSet))
                    {
                        isSame = true;
                        canonicalCollection.get(index).GetTransitionMap().put(markSymbol, lr1State);
                    }
                }

                if (isSame == false)
                {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(index).GetTransitionMap().put(markSymbol, nextState);
                }
            }
        }

    }

    private void GenerateState_LALR()
    {
        GenerateStates_CLR();

        ArrayList<LR1State> newCanonicalCollection = new ArrayList<>();

        for (int i = 0; i < canonicalCollection.size(); i++)
        {
            HashSet<LR0Item> lr0ItemSetI = new HashSet<>();

            for (LR1Item lr1Item : canonicalCollection.get(i).GetLR1ItemSet())
            {
                lr0ItemSetI.add(new LR0Item(lr1Item.GetLeftSymbol(), lr1Item.GetRightSymbolArray(), lr1Item.GetDotSymbolPosition()));
            }

            for (int j = i + 1; j < canonicalCollection.size(); j++)
            {
                HashSet<LR0Item> lr0ItemSetJ = new HashSet<>();

                for (LR1Item item : canonicalCollection.get(j).GetLR1ItemSet())
                {
                    lr0ItemSetJ.add(new LR0Item(item.GetLeftSymbol(), item.GetRightSymbolArray(), item.GetDotSymbolPosition()));
                }

                if (lr0ItemSetI.containsAll(lr0ItemSetJ) && lr0ItemSetJ.containsAll(lr0ItemSetI))
                {
                    for (LR1Item itemI : canonicalCollection.get(i).GetLR1ItemSet())
                    {
                        for (LR1Item itemJ : canonicalCollection.get(j).GetLR1ItemSet())
                        {
                            if (itemI.equalLR0(itemJ))
                            {
                                itemI.GetLookAheadSet().addAll(itemJ.GetLookAheadSet());
                                break;
                            }
                        }
                    }

                    for (LR1State lr1State : canonicalCollection)
                    {
                        for (String symbol : lr1State.GetTransitionMap().keySet())
                        {
                            if (lr1State.GetTransitionMap().get(symbol).GetLR1ItemSet().containsAll(canonicalCollection.get(j).GetLR1ItemSet()) && canonicalCollection.get(
                                    j).GetLR1ItemSet().containsAll(lr1State.GetTransitionMap().get(symbol).GetLR1ItemSet()))
                            {
                                lr1State.GetTransitionMap().put(symbol, canonicalCollection.get(i));
                            }
                        }
                    }
                    canonicalCollection.remove(j);
                    j--;
                }
            }
            newCanonicalCollection.add(canonicalCollection.get(i));
        }

        canonicalCollection = newCanonicalCollection;
    }

    protected void GenerateGoToTable()
    {
        GoToTable = new ArrayList<>();

        for (LR1State lr1State : canonicalCollection)
        {
            HashMap<String, Integer> gotoState = new HashMap<>();
            for (String symbol : lr1State.GetTransitionMap().keySet())
            {
                if (grammar.IsNonTerminal(symbol))
                {
                    gotoState.put(symbol, FindLR1StateIndex(lr1State.GetTransitionMap().get(symbol)));
                }
            }
            GoToTable.add(gotoState);
        }
    }

    private boolean GenerateActionTable()
    {
        actionTable = new ArrayList<>();

        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            HashMap<String, LR1State> transitionMap = canonicalCollection.get(index).GetTransitionMap();

            actionTable.add(new HashMap<>());
            for (String symbol : transitionMap.keySet())
            {
                if (grammar.GetTerminalSet().contains(symbol))
                {
                    actionTable.get(index).put(symbol, new Action(Action.ActionType.Shift, FindLR1StateIndex(transitionMap.get(symbol))));
                }
            }
        }

        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            for (LR1Item lr1Item : canonicalCollection.get(index).GetLR1ItemSet())
            {
                if (lr1Item.GetDotSymbolPosition() == lr1Item.GetRightSymbolArray().length)
                {
                    if (lr1Item.GetLeftSymbol().equals("S'"))
                    {
                        actionTable.get(index).put("$", new Action(Action.ActionType.Accept, 0));
                    }
                    else
                    {
                        Rule rule = new Rule(lr1Item);
                        Action action = new Action(Action.ActionType.Reduce, grammar.FindRuleIndex(rule));

                        for (String symbol : lr1Item.GetLookAheadSet())
                        {
                            if (actionTable.get(index).get(symbol) != null)
                            {
                                System.out.println(String.format("%s-%s 충돌이 발생하였습니다, '%d'번 상태와 '%s'터미널 심볼",
                                                                 Action.ActionType.Reduce, actionTable.get(index).get(symbol).GetActionType(), index, symbol));
//                                return false;
                            }
                            else
                            {
                                actionTable.get(index).put(symbol, action);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private int FindLR1StateIndex(LR1State state)
    {
        return IntStream.range(0, canonicalCollection.size()).filter(i -> canonicalCollection.get(i).equals(state)).findFirst().orElse(-1);
    }
}
