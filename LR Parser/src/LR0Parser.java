import java.util.*;
import java.util.stream.IntStream;

public class LR0Parser extends LRParser
{
    private ArrayList<LR0State> canonicalCollection;

    public LR0Parser(Grammar grammar)
    {
        super(grammar);
    }

    public boolean TryParseSLR()
    {
        GenerateState();
        GenerateGoToTable();

        return GenerateActionTable_SLR();
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

    private void GenerateState()
    {
        canonicalCollection = new ArrayList<>();

        HashSet<LR0Item> startItem = new HashSet<>();
        startItem.add(new LR0Item(grammar.GetRuleList().get(0)));
        canonicalCollection.add(new LR0State(grammar, startItem));

        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            HashSet<String> markSymbolSet = new HashSet<>();
            for (LR0Item lr0Item : canonicalCollection.get(index).GetLR0ItemSet())
            {
                String markSymbol = lr0Item.GetMarkSymbol();
                if (markSymbol != null)
                {
                    markSymbolSet.add(markSymbol);
                }
            }

            for (String markSymbol : markSymbolSet)
            {
                HashSet<LR0Item> nextStateItemSet = new HashSet<>();
                for (LR0Item lr0Item : canonicalCollection.get(index).GetLR0ItemSet())
                {
                    if (lr0Item.GetMarkSymbol() != null && lr0Item.GetMarkSymbol().equals(markSymbol))
                    {
                        LR0Item shiftedItem = new LR0Item(lr0Item);
                        shiftedItem.ShiftDotPosition();
                        nextStateItemSet.add(shiftedItem);
                    }
                }

                LR0State nextState = new LR0State(grammar, nextStateItemSet);

                boolean isSame = false;
                for (LR0State lr0State : canonicalCollection)
                {
                    HashSet<LR0Item> lr0ItemSet = lr0State.GetLR0ItemSet();
                    HashSet<LR0Item> nextStateLR0ItemSet = nextState.GetLR0ItemSet();

                    if (lr0ItemSet.containsAll(nextStateLR0ItemSet) && nextStateLR0ItemSet.containsAll(lr0ItemSet))
                    {
                        isSame = true;
                        canonicalCollection.get(index).AddTransition(markSymbol, lr0State);
                    }
                }

                if (isSame == false)
                {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(index).AddTransition(markSymbol, nextState);
                }
            }
        }
    }

    protected void GenerateGoToTable()
    {
        GoToTable = new ArrayList<>();

        for (LR0State lr0State : canonicalCollection)
        {
            HashMap<String, Integer> gotoState = new HashMap<>();
            for (String symbol : lr0State.GetTransitionMap().keySet())
            {
                if (grammar.GetNonTerminalSet().contains(symbol))
                {
                    gotoState.put(symbol, FindLR0StateIndex(lr0State.GetTransitionMap().get(symbol)));
                }
            }
            GoToTable.add(gotoState);
        }
    }

    private void GenerateActionTableBase()
    {
        actionTable = new ArrayList<>();

        for (LR0State lr0State : canonicalCollection)
        {
            HashMap<String, Action> state = new HashMap<>();
            for (String symbol : lr0State.GetTransitionMap().keySet())
            {
                state.put(symbol, new Action(Action.ActionType.Shift, FindLR0StateIndex(lr0State.GetTransitionMap().get(symbol))));
            }
            actionTable.add(state);
        }
    }

    private boolean GenerateActionTable_SLR()
    {
        GenerateActionTableBase();

        for (int index = 0; index < canonicalCollection.size(); index++)
        {
            for (LR0Item item : canonicalCollection.get(index).GetLR0ItemSet())
            {
                if (item.GetDotSymbolPosition() == item.GetRightSymbolArray().length)
                {
                    Map<String, Action> state = actionTable.get(index);
                    if (item.GetLeftSymbol().equals("S'"))
                    {
                        state.put("$", new Action(Action.ActionType.Accept, 0));
                        continue;
                    }

                    HashSet<String> followSet = grammar.GetFollowMap().get(item.GetLeftSymbol());
                    HasConflict(index, item, state, followSet);
                }
            }
        }
        return true;
    }

    private boolean HasConflict(int stateNumber, LR0Item item, Map<String, Action> state, HashSet<String> symbolSet)
    {
        Rule rule = new Rule(item);
        Action action = new Action(Action.ActionType.Reduce, grammar.FindRuleIndex(rule));

        for (String symbol : symbolSet)
        {
            if (state.get(symbol) != null)
            {
                System.out.println(String.format("%s-%s 충돌이 발생하였습니다, '%d'번 상태와 '%s'터미널 심볼",
                                                 Action.ActionType.Reduce, state.get(symbol).GetActionType(), stateNumber, symbol));
                return true;
            }
            else
            {
                state.put(symbol, action);
            }
        }
        return false;
    }

    private int FindLR0StateIndex(LR0State state)
    {
        return IntStream.range(0, canonicalCollection.size()).filter(i -> canonicalCollection.get(i).equals(state)).findFirst().orElse(-1);
    }
}
