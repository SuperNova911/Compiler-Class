package supernova;

import java.io.*;

public class Main
{
    private static final String GRAMMAR1_PATH = "Example/Grammar1";
    private static final String GRAMMAR2_PATH = "Example/Grammar2";
    private static final String GRAMMAR3_PATH = "Example/Grammar3";
    private static final String GRAMMAR4_PATH = "Example/Grammar4";

    public static void main(String[] args)
    {
        String input = ReadFile(GRAMMAR4_PATH);

        System.out.println(input);

        GrammarSet grammarSet = GrammarSetGenerator.ParseGrammar(input);
        System.out.println(String.format("Parsed Grammar\n%s", grammarSet.toString()));

        grammarSet.RemoveRightRecursive();
        System.out.println(String.format("Remove Right Recursive Grammar\n%s", grammarSet.toString()));

        grammarSet.GenerateFirst();
        for (Grammar grammar : grammarSet.GetGrammarList())
        {
            System.out.println(String.format("FIRST(%s) = %s", grammar.GetLeftSymbol(), grammar.GetFirstSymbolList()));
        }
        System.out.println();

        grammarSet.GenerateFollow();
        for (Grammar grammar : grammarSet.GetGrammarList())
        {
            System.out.println(String.format("FOLLOW(%s) = %s", grammar.GetLeftSymbol(), grammar.GetFollowSymbolList()));
        }
        System.out.println();

        grammarSet.AugmentGrammar();
        System.out.println(String.format("Augmented Grammar\n%s", grammarSet.toString()));
    }

    private static String ReadFile(String path)
    {
        StringBuilder stringBuilder = new StringBuilder();
        String nextLine;

        try
        {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((nextLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(nextLine);
                stringBuilder.append(Grammar.DELIM_STRING);
            }
        }
        catch (IOException e)
        {
            System.out.println(String.format("파일을 읽을 수 없습니다. path: '%s'", path));
        }

        return stringBuilder.toString();
    }
}