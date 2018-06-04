import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.*;

public class XMLMaskUtil {

    private static final int MAX_MASK_SIZE = 4;

    public static void main(String[] args) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get( "src", "main.xml")));
        System.out.println(XMLMaskUtil.mask(content, new String[]{"soap:Envelope"}));
    }

    public static String mask(String content, String... fields) {
        for(String field : fields) {
            String regex = "(?:<"+field+"\\b.*?>)(.*?(?=</\\s*"+field+"\\s*>))";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            while(matcher.find()) {
                content = new StringBuilder(content).replace(matcher.start(1), matcher.end(1), genMask(matcher.group(1).length())).toString();
            }
        }
	//normalize mask
	return content.replaceAll("(?<=>)\\*+\\s*?(?=</)", genCharSeq(MAX_MASK_SIZE, "*"));        
    }

    private static String genMask(int size) {
        int maskSize = size;
        int whiteSpaceSize = 0;
        if (size >= MAX_MASK_SIZE) {
            maskSize = MAX_MASK_SIZE;
            whiteSpaceSize = Math.abs(size - maskSize);
        }
        //basically, size = maskSize + whiteSpaceSize - equation 1
        return genCharSeq(maskSize, "*") + genCharSeq(whiteSpaceSize, " ") ;
    }

    private static String genCharSeq(int size, String sChar) {
        return new String(new char[size]).replace("\0", sChar);
    }
}