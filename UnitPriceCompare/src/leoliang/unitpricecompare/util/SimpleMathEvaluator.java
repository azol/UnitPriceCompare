package leoliang.unitpricecompare.util;

import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * From http://blog.csdn.net/aoyihuashao/archive/2007/06/12/1649205.aspx
 */
public class SimpleMathEvaluator {

    /**
     * Evaluate the value of an math expression
     * 
     * @param expression
     * @return value of the expression
     * @throws ArithmeticException
     */
    public static double eval(String expression) throws ArithmeticException {
        SimpleMathEvaluator evaluator = new SimpleMathEvaluator();
        //以下对输入字符串做规则处理
        String validExpression = evaluator.validateExpression(expression);
        //以下对输入字符串做表达式转换
        Vector<String> v_compute = evaluator.getExpression(validExpression);
        //以下进行后缀表达式转换
        Vector<String> v_tmp_prefix = evaluator.transformPrefix(v_compute);
        //以下进行后缀表达式运算
        return evaluator.evaluatePrefix(v_tmp_prefix);
    }

    /**
     *输入字符串转换.把从控制台读入的字符串转成表达式存在一个队列中. 例:123+321 存为"123""+""321"
     */
    private Vector<String> getExpression(String str) {
        Vector<String> v_temp = new Vector<String>();
        char[] temp = new char[str.length()];
        str.getChars(0, str.length(), temp, 0);
        String fi = "";
        int i = 0;
        String regex_fig = "[\\.\\d]"; //匹配数字和小数点
        String regex_operator = "[\\+\\-\\*/\\(\\)]"; //匹配运算符(+,-,*,/)和括号("(",")")
        Pattern p_fig = Pattern.compile(regex_fig);
        Pattern p_operator = Pattern.compile(regex_operator);
        Matcher m = null;
        boolean b;
        while (i < str.length()) {
            Character c = new Character(temp[i]);
            String s = c.toString();
            //System.out.println("char c = "+s);
            m = p_operator.matcher(s);
            b = m.matches();

            if (b) {
                //System.out.println("matches operator");
                v_temp.add(fi);
                fi = "";
                v_temp.add(s);
            }
            m = p_fig.matcher(s);
            b = m.matches();
            if (b) {
                //System.out.println("matches fig");
                fi = fi + s;
            }
            i++;
        }
        v_temp.add(fi);

        return v_temp;
    }

    /**
     *转换中序表示式为前序表示式
     */
    private Vector<String> transformPrefix(Vector<String> v_expression) {
        Vector<String> v_prefix = new Vector<String>();
        Stack<String> s_tmp = new Stack<String>();
        String regex_float = "\\d+(\\.\\d+)?"; //匹配正浮点数
        Pattern p_float = Pattern.compile(regex_float);
        Matcher m = null;
        boolean b;
        String str_elem = "";

        for (int i = 0; i < v_expression.size(); i++) {
            str_elem = v_expression.get(i).toString();
            m = p_float.matcher(str_elem);
            b = m.matches();

            if (b) {
                v_prefix.add(str_elem);
            }

            if (str_elem.equals("+") || str_elem.equals("-")) {
                if (s_tmp.isEmpty()) {
                    s_tmp.push(str_elem);
                } else {
                    while (!s_tmp.isEmpty()) {
                        String str_tmp = s_tmp.peek();

                        if (str_tmp.equals("(")) {
                            break;
                        }
                        v_prefix.add(s_tmp.pop());
                    }
                    s_tmp.push(str_elem);
                }
            }

            if (str_elem.equals("*") || str_elem.equals("/")) {
                if (s_tmp.isEmpty()) {
                    s_tmp.push(str_elem);
                } else {
                    while (!s_tmp.isEmpty()) {
                        String str_tmp = s_tmp.peek();

                        if (str_tmp.equals("(") || str_tmp.equals("+") || str_tmp.equals("-")) {
                            break;
                        }
                        v_prefix.add(s_tmp.pop());
                    }
                    s_tmp.push(str_elem);
                }
            }

            if (str_elem.equals("(")) {
                s_tmp.push(str_elem);
            }

            if (str_elem.equals(")")) {
                while (!s_tmp.isEmpty()) {
                    String str_tmp = s_tmp.peek();
                    if (str_tmp.equals("(")) {
                        s_tmp.pop();
                        break;
                    }
                    v_prefix.add(s_tmp.pop());
                }
            }
        }

        while (!s_tmp.isEmpty()) {
            v_prefix.add(s_tmp.pop());
        }
        return v_prefix;
    }

    /**
     *前缀表示式求值
     */
    private strictfp double evaluatePrefix(Vector<String> v_prefix) {
        String str_tmp = "";
        double num1, num2, interAns = 0;
        Stack<Double> s_compute = new Stack<Double>();

        int i = 0;
        while (i < v_prefix.size()) {
            str_tmp = v_prefix.get(i).toString();
            if (!str_tmp.equals("+") && !str_tmp.equals("-") && !str_tmp.equals("*") && !str_tmp.equals("/")) {
                interAns = s_compute.push(Double.parseDouble(str_tmp));
            } else {
                num2 = (s_compute.pop());
                num1 = (s_compute.pop());

                if (str_tmp.equals("+")) {
                    interAns = num1 + num2;
                }
                if (str_tmp.equals("-")) {
                    interAns = num1 - num2;
                }
                if (str_tmp.equals("*")) {
                    interAns = num1 * num2;
                }
                if (str_tmp.equals("/")) {
                    interAns = num1 / num2;
                }
                s_compute.push(interAns);
            }
            i++;
        }
        return interAns;
    }

    /**
     *括号匹配检测
     */
    private boolean checkBracket(String str) {
        Stack<Character> s_check = new Stack<Character>();
        boolean b_flag = true;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
            case '(':
                s_check.push(ch);
                break;
            case ')':
                if (!s_check.isEmpty()) {
                    char chx = s_check.pop();
                    if ((ch == ')') && (chx != '(')) {
                        b_flag = false;
                    }
                } else {
                    b_flag = false;
                }
                break;
            default:
                break;
            }
        }
        if (!s_check.isEmpty()) {
            b_flag = false;
        }
        return b_flag;
    }

    /**
     *表达式正确性规则处理与校验
     */
    private String validateExpression(String expression) throws ArithmeticException {
        Stack<Character> s_check = new Stack<Character>();
        Stack<Character> s_tmp = new Stack<Character>();
        String str_result = "";

        String str_regex = "^[\\.\\d\\+\\-\\*/\\(\\)]+$"; //匹配合法的运算字符"数字,.,+,-,*,/,(,),"
        Pattern p_filtrate = Pattern.compile(str_regex);
        Matcher m = p_filtrate.matcher(expression);
        boolean b_filtrate = m.matches();
        if (!b_filtrate) {
            throw new ArithmeticException("Invalid expression: " + expression);
        }

        String str_err_float = ".*(\\.\\d*){2,}.*"; //匹配非法的浮点数.
        Pattern p_err_float = Pattern.compile(str_err_float);
        Matcher m_err_float = p_err_float.matcher(expression);
        boolean b_err_float = m_err_float.matches();
        if (b_err_float) {
            throw new ArithmeticException("Invalid expression: " + expression);
        }

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (checkFig(ch)) {
                if (!s_tmp.isEmpty() && (s_tmp.peek() == ')')) {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                s_tmp.push(ch);
                str_result = str_result + ch;
            }

            switch (ch) {
            case '(':
                if (!s_tmp.isEmpty() && (s_tmp.peek() == '.')) {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                s_check.push(ch);
                if (s_tmp.isEmpty() || (!SimpleMathEvaluator.checkFig(s_tmp.peek()) && (s_tmp.peek() != ')'))) {
                    str_result = str_result + ch;
                } else {
                    str_result = str_result + "*" + ch;
                }
                s_tmp.push(ch);
                break;
            case ')':
                if (!s_check.isEmpty()) {
                    char chx = s_check.pop();
                    if ((ch == ')') && (chx != '(')) {
                        throw new ArithmeticException("Invalid expression: " + expression);
                    }
                } else {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                if ((s_tmp.peek() == '.') || (!SimpleMathEvaluator.checkFig(s_tmp.peek()) && (s_tmp.peek() != ')'))) {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                s_tmp.push(ch);
                str_result = str_result + ch;
                break;
            case '+':
            case '-':
                if (!s_tmp.isEmpty()
                        && ((s_tmp.peek() == '+') || (s_tmp.peek() == '-') || (s_tmp.peek() == '*')
                                || (s_tmp.peek() == '/') || (s_tmp.peek() == '.'))) {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                if (s_tmp.isEmpty() || (s_tmp.peek() == '(')) {
                    str_result = str_result + "0" + ch;
                } else {
                    str_result = str_result + ch;
                }
                s_tmp.push(ch);
                break;
            case '*':
            case '/':
                if (s_tmp.isEmpty() || (s_tmp.peek() == '.')
                        || (!SimpleMathEvaluator.checkFig(s_tmp.peek()) && (s_tmp.peek() != ')'))) {
                    throw new ArithmeticException("Invalid expression: " + expression);
                }
                s_tmp.push(ch);
                str_result = str_result + ch;
                break;
            case '.':
                if (s_tmp.isEmpty() || !SimpleMathEvaluator.checkFig(s_tmp.peek())) {
                    str_result = str_result + "0" + ch;
                } else {
                    str_result = str_result + ch;
                }
                s_tmp.push(ch);
                break;

            default:
                break;
            }
        }
        if (!s_check.isEmpty()) {
            throw new ArithmeticException("Invalid expression: " + expression);
        }

        return str_result;
    }

    private static boolean checkFig(Object ch) {
        String s = ch.toString();
        String str_regexfig = "\\d"; //匹配数字
        Pattern p_fig = Pattern.compile(str_regexfig);
        Matcher m_fig = p_fig.matcher(s);
        boolean b_fig = m_fig.matches();
        return b_fig;
    }
}