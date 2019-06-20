import java.io.Serializable;
import java.util.*;

public abstract class LRParser implements Serializable
{
    protected final Grammar grammar;
    protected ArrayList<Map<String, Action>> actionTable;
    protected ArrayList<Map<String, Integer>> GoToTable;

    public LRParser(Grammar grammar)
    {
        this.grammar = grammar;
    }

    protected abstract void GenerateGoToTable();

    public boolean IsAcceptable(ArrayList<String> inputBuffer)
    {
        Stack<String> stack = new Stack<>();
        stack.push("0");

        inputBuffer.add("$");
        int counter = 0;
        while (inputBuffer.isEmpty() == false)
        {
            String nextSymbol = inputBuffer.get(0);
            Action currentAction = actionTable.get(Integer.valueOf(stack.peek())).get(nextSymbol);
            if (currentAction == null)
            {
                return false;
            }

            System.out.println(String.format("단계: %d \t스택: %-40s \t\t입력 기호: %-40s \t\t구문 분석 내용: %s", counter, stack, inputBuffer, currentAction));
            counter++;

            switch (currentAction.GetActionType())
            {
                case Shift:
                    stack.push(nextSymbol);
                    stack.push(currentAction.GetTargetStateNumber() + "");
                    inputBuffer.remove(0);
                    break;

                case Reduce:
                    Rule rule = grammar.GetRuleList().get(currentAction.GetTargetStateNumber());
                    String leftSymbol = rule.GetLeftSymbol();
                    String[] rightSymbolArray = rule.GetRightSymbolArray();

                    for (int index = 0; index < rightSymbolArray.length; index++)
                    {
                        stack.pop();
                        stack.pop();
                    }

                    int targetState = GoToTable.get(Integer.valueOf(stack.peek())).get(leftSymbol);
                    stack.push(leftSymbol);

                    System.out.println(String.format("단계: %d \t스택: %-40s \t\t입력 기호: %-40s \t\t구문 분석 내용: %s", counter, stack, inputBuffer, String.format("GOTO %d", targetState)));
                    counter++;

                    stack.push(targetState + "");
                    break;

                case Accept:
                    return true;
            }
        }

        return false;
    }

    public String PrintParseTable()
    {
        StringBuilder stringBuilder = new StringBuilder();

        HashSet<String> nonTerminalSet = new HashSet<>(grammar.GetNonTerminalSet());
        HashSet<String> terminalSet = new HashSet<>(grammar.GetTerminalSet());
        terminalSet.add("$");

        stringBuilder.append("┼");
        stringBuilder.append("──────────┼".repeat(Math.max(0, terminalSet.size() + nonTerminalSet.size() + 1)));
        stringBuilder.append("\n");

        stringBuilder.append("│");
        stringBuilder.append(String.format(" %-9s│", ""));
        stringBuilder.append(String.format(" %-16s  ", "Parser Action"));
        for (int i = 2; i < terminalSet.size(); i++)
        {
            if (i + 1 == terminalSet.size())
            {
                stringBuilder.append(String.format(" %-9s│", ""));
            }
            else
            {
                stringBuilder.append(String.format(" %-9s  ", ""));
            }
        }
        stringBuilder.append(String.format(" %-9s", "GOTO"));
        for (int i = 1; i < nonTerminalSet.size(); i++)
        {
            if (i + 1 == nonTerminalSet.size())
            {
                stringBuilder.append(String.format(" %-9s│", ""));
            }
            else
            {
                stringBuilder.append(String.format(" %-9s  ", ""));
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("┼");
        stringBuilder.append("──────────┼".repeat(Math.max(0, terminalSet.size() + nonTerminalSet.size() + 1)));
        stringBuilder.append("\n");

        stringBuilder.append("│");
        stringBuilder.append(String.format(" %-9s│", "State"));
        for (String terminalSymbol : terminalSet)
        {
            stringBuilder.append(String.format(" %-9s│", terminalSymbol));
        }
        for (String nonTerminalSymbol : nonTerminalSet)
        {
            stringBuilder.append(String.format(" %-9s│", nonTerminalSymbol));
        }
        stringBuilder.append("\n");

        for (int index = 0; index < actionTable.size(); index++)
        {
            stringBuilder.append("┼");
            stringBuilder.append("──────────┼".repeat(Math.max(0, terminalSet.size() + nonTerminalSet.size() + 1)));
            stringBuilder.append("\n");

            stringBuilder.append("│");
            stringBuilder.append(String.format(" %-9s│", index));

            for (String terminalSymbol : terminalSet)
            {
                Action action = actionTable.get(index).get(terminalSymbol);
                stringBuilder.append(String.format(" %-9s│", action == null ? "" : action ));
            }

            for (String nonTerminalSymbol : nonTerminalSet)
            {
                Integer target = GoToTable.get(index).get(nonTerminalSymbol);
                stringBuilder.append(String.format(" %-9s│", target == null ? "" : target));
            }

            stringBuilder.append("\n");
        }

        stringBuilder.append("┼");
        stringBuilder.append("──────────┼".repeat(Math.max(0, terminalSet.size() + nonTerminalSet.size() + 1)));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public Grammar GetGrammar()
    {
        return grammar;
    }
}
