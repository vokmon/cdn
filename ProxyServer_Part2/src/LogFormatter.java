import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Log formatter
 * 
 * @author @author Arnon  Ruangthanawes arua663
 */
public class LogFormatter extends Formatter{

	@Override
	public String format(LogRecord record) {
		String s = String.format("%s %s: %s \n", 
				new Date(record.getMillis()).toString(),
				record.getLevel().toString(), 
				record.getMessage());
		return s;
	}
}
