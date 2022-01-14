public class Main {


    public static void main(String[] args) throws Exception{
        String queriesPath = Main.class.getResource("queries").getPath();
        Generator generator = new Generator(queriesPath,"result");
        generator.setSizes(new int[]{5, 10});
        generator.generateTestCases();
    }
}
