package supernova;

public class EndMarker extends Symbol
{
    public EndMarker()
    {
        super(Grammar.END_MARKER_STRING, SymbolType.EndMarker, false);
    }
}
