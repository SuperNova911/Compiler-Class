import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main
{
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        while (true)
        {
            MainMenu();
        }
    }

    private static void MainMenu()
    {
        System.out.println("1. FIRST 계산, 2. FOLLOW 계산, 3. SLR 파싱, 4. CLR 파싱, 5. LALR 파싱, 6. LR 구문 분석");
        System.out.print("입력: ");

        switch (scanner.nextInt())
        {
            case 1:
                FirstMenu();
                break;
            case 2:
                FollowMenu();
                break;
            case 3:
                while (SLR_Menu()) {}
                break;
            case 4:
                while (CLR_Menu()) {}
                break;
            case 5:
                while (LALR_Menu()) {}
                break;
            case 6:
                LR_Menu();
                break;
            default:
                System.out.println("잘못된 메뉴 번호 입력");
                break;
        }
    }

    private static void FirstMenu()
    {
        System.out.println("FIRST를 계산할 문법 파일 이름을 입력하세요, 0: 취소");
        System.out.print("입력: ");
        String userInput = scanner.next();
        if (userInput.equals("0"))
        {
            return;
        }

        Grammar grammar = new Grammar(ReadFile(userInput, " "));
        if (grammar.GetNonTerminalSet().size() < 1)
        {
            System.out.println("FIRST 계산을 위해 생성된 문법이 없습니다");
            return;
        }

        System.out.println(grammar.PrintFirstSet());
    }

    private static void FollowMenu()
    {
        System.out.println("FOLLOW를 계산할 문법 파일 이름을 입력하세요, 0: 취소");
        System.out.print("입력: ");
        String userInput = scanner.next();
        if (userInput.equals("0"))
        {
            return;
        }

        Grammar grammar = new Grammar(ReadFile(userInput, " "));
        if (grammar.GetNonTerminalSet().size() < 1)
        {
            System.out.println("FOLLOW 계산을 위해 생성된 문법이 없습니다");
            return;
        }

        System.out.println(grammar.PrintFollowSet());
    }

    private static boolean SLR_Menu()
    {
        System.out.println("\n1. SLR 파싱표 만들기, 2. SLR 파싱표 보기, 0. 메인 메뉴로 돌아가기");
        System.out.print("입력: ");

        String userInput;
        LR0Parser parser;

        switch (scanner.nextInt())
        {
            case 1:
                System.out.println("SLR 파싱표를 만들기 위한 문법 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                Grammar grammar = new Grammar(ReadFile(userInput, " "));
                if (grammar.GetNonTerminalSet().size() < 1)
                {
                    System.out.println("생성된 문법이 없습니다");
                    break;
                }

                parser = new LR0Parser(grammar);

                System.out.println("SLR 파싱 시도");
                parser.TryParseSLR();
                System.out.println("파싱표 생성 완료");

                System.out.println("파싱표를 저장할 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                if (SaveParser(parser, userInput))
                {
                    System.out.println(String.format("파싱표 저장 성공, 파일 이름: '%s'", userInput));
                }
                else
                {
                    System.out.println("파싱표 저장 실패");
                }
                break;

            case 2:
                System.out.println("SLR 파싱표 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                Object loadedParser = LoadLRParseTable(userInput);
                if (loadedParser == null)
                {
                    System.out.println("파싱표를 불러오지 못했습니다");
                    break;
                }
                if (loadedParser instanceof LR0Parser == false)
                {
                    System.out.println("인식할 수 없는 파싱표 입니다. LR0과 LR1 파싱표는 서로 호환되지 않습니다");
                    break;
                }

                parser = (LR0Parser) loadedParser;

                System.out.println(parser.PrintParseTable());
                System.out.println(parser.PrintCanonicalCollection());
                System.out.println(parser.GetGrammar().PrintFollowSet());
                break;

            default:
                return false;
        }

        return true;
    }

    private static boolean CLR_Menu()
    {
        System.out.println("\n1. CLR 파싱표 만들기, 2. CLR 파싱표 보기, 0. 메인 메뉴로 돌아가기");
        System.out.print("입력: ");

        String userInput;
        LR1Parser parser;

        switch (scanner.nextInt())
        {
            case 1:
                System.out.println("CLR 파싱표를 만들기 위한 문법 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }
                Grammar grammar = new Grammar(ReadFile(userInput, " "));
                if (grammar.GetNonTerminalSet().size() < 1)
                {
                    System.out.println("생성된 문법이 없습니다");
                    break;
                }

                parser = new LR1Parser(grammar);

                System.out.println("CLR 파싱 시도");
                parser.TryParseCLR();
                System.out.println("파싱표 생성 완료");

                System.out.println("파싱표를 저장할 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                if (SaveParser(parser, userInput))
                {
                    System.out.println(String.format("파싱표 저장 성공, 파일 이름: '%s'", userInput));
                }
                else
                {
                    System.out.println("파싱표 저장 실패");
                }
                break;

            case 2:
                System.out.println("CLR 파싱표 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                Object loadedParser = LoadLRParseTable(userInput);
                if (loadedParser == null)
                {
                    System.out.println("파싱표를 불러오지 못했습니다");
                    break;
                }
                if (loadedParser instanceof LR1Parser == false)
                {
                    System.out.println("인식할 수 없는 파싱표 입니다. LR0과 LR1 파싱표는 서로 호환되지 않습니다");
                    break;
                }

                parser = (LR1Parser) loadedParser;

                System.out.println(parser.PrintParseTable());
                System.out.println(parser.PrintCanonicalCollection());
                break;

            default:
                return false;
        }

        return true;
    }

    private static boolean LALR_Menu()
    {
        System.out.println("\n1. LALR 파싱표 만들기, 2. LALR 파싱표 보기, 0. 메인 메뉴로 돌아가기");
        System.out.print("입력: ");

        String userInput;
        LR1Parser parser;

        switch (scanner.nextInt())
        {
            case 1:
                System.out.println("LALR 파싱표를 만들기 위한 문법 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }
                Grammar grammar = new Grammar(ReadFile(userInput, " "));
                if (grammar.GetNonTerminalSet().size() < 1)
                {
                    System.out.println("생성된 문법이 없습니다");
                    break;
                }

                parser = new LR1Parser(grammar);

                System.out.println("LALR 파싱 시도");
                parser.TryParseLALR();
                System.out.println("파싱표 생성 완료");

                System.out.println("파싱표를 저장할 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                if (SaveParser(parser, userInput))
                {
                    System.out.println(String.format("파싱표 저장 성공, 파일 이름: '%s'", userInput));
                }
                else
                {
                    System.out.println("파싱표 저장 실패");
                }
                break;

            case 2:
                System.out.println("LALR 파싱표 파일의 이름을 입력하세요, 0: 취소");
                System.out.print("입력: ");
                userInput = scanner.next();
                if (userInput.equals("0"))
                {
                    break;
                }

                Object loadedParser = LoadLRParseTable(userInput);
                if (loadedParser == null)
                {
                    System.out.println("파싱표를 불러오지 못했습니다");
                    break;
                }
                if (loadedParser instanceof LR1Parser == false)
                {
                    System.out.println("인식할 수 없는 파싱표 입니다. LR0과 LR1 파싱표는 서로 호환되지 않습니다");
                    break;
                }

                parser = (LR1Parser) loadedParser;

                System.out.println(parser.PrintParseTable());
                System.out.println(parser.PrintCanonicalCollection());
                break;

            default:
                return false;
        }

        return true;
    }

    private static void LR_Menu()
    {
        String userInput;

        System.out.println("\n불러올 파싱표 파일의 이름을 입력하세요, 0: 취소");
        System.out.print("입력: ");
        userInput = scanner.next();
        if (userInput.equals("0"))
        {
            return;
        }

        Object loadedParser = LoadLRParseTable(userInput);
        if (loadedParser == null)
        {
            System.out.println("파싱표를 불러오지 못했습니다");
            return;
        }

        LRParser parser;
        if (loadedParser instanceof LR0Parser)
        {
            parser = (LR0Parser)loadedParser;
        }
        else if (loadedParser instanceof LR1Parser)
        {
            parser = (LR1Parser)loadedParser;
        }
        else
        {
            System.out.println("인식할 수 없는 파싱표 입니다");
            return;
        }
        System.out.println("파싱표를 성공적으로 불러왔습니다");

        System.out.println("문법을 테스트할 입력 문장 파일의 이름을 입력하세요, 0: 취소");
        System.out.print("입력: ");
        userInput = scanner.next();
        if (userInput.equals("0"))
        {
            return;
        }

        Scanner inputScanner = new Scanner(ReadFile(userInput, "\n"));
        while (inputScanner.hasNext())
        {
            String testInput = inputScanner.nextLine();
            System.out.println(String.format("테스트 할 문장: '%s'", testInput));

            boolean isAcceptable = parser.IsAcceptable(new ArrayList<>(Arrays.asList(testInput.split("\\s+"))));
            System.out.println(String.format("%s\n", isAcceptable ? "문법에 맞는 문장입니다" : "문법에 맞지 않는 문장입니다"));
        }
    }

    private static String ReadFile(String path, String lineDelim)
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
                stringBuilder.append(lineDelim);
            }
        }
        catch (IOException e)
        {
            System.out.println(String.format("파일을 읽을 수 없습니다. path: '%s'", path));
        }

        return stringBuilder.toString();
    }

    private static boolean SaveParser(LRParser parser, String path)
    {
        FileOutputStream fileOutputStream;
        try
        {
            fileOutputStream = new FileOutputStream(path);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(String.format("'%s' 잘못된 파일 이름", path));
            return false;
        }

        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(parser);
            objectOutputStream.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println(String.format("'%s' 파일 쓰기 실패", path));
            return false;
        }
    }

    private static Object LoadLRParseTable(String path)
    {
        FileInputStream fileInputStream;
        try
        {
            fileInputStream = new FileInputStream(path);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(String.format("'%s' 잘못된 파일 이름", path));
            return null;
        }

        try
        {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object obj = objectInputStream.readObject();
            objectInputStream.close();

            return obj;
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println(String.format("'%s' 파일 읽기 실패", path));
        }

        return null;
    }
}