package com.teachmeskills.Lesson_16;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
  Программа на вход получает путь к папке (задается через консоль).
  В заданной папке находятся текстовые файлы (формат тхт).
  Каждый файл содержит произвольный текст. В этом тексте может быть номер документа(один или несколько), емейл и номер телефона
  номер документа в формате: xxxx-yyy-xxxx-yyy-xyxy, где x - это любая цифра, а y - это любая буква русского или латинского алфавита
  номер телефона в формате: +(ХХ)ХХХХХХХ
  Документ может содержать не всю информацию, т.е. например, может не содержать номер телефона, или другое поле.
  Необходимо извлечь информацию из N текстовых документов. Число документов для обработки N задается с консоли.
  Если в папке содержится меньше документов, чем заданое число - следует обрабатывать все документы.
  Извлеченную информацию необходимо сохранить в следующую стурктуру данных:
  Map<String, Document>, где
  ключ типа String - это имя документа без расширения,
  значение типа Document - объект кастомного класса, поля которого содержат извлеченную из текстового документа информацию

  Учесть вывод сообщений на случаи если,
  - на вход передан путь к папке, в которой нет файлов
  - все файлы имеют непоходящий формат (следует обрабатывать только тхт файлы)
  - так же сообщения на случай других исключительных ситуаций

  В конце работы программы следует вывести сообщение о том, сколько документов обработано и сколько было документов невалидного формата.
 */

public class Parser implements IParser{

    @Override
    public void parse (String pathToFolder, int countToParse) {

        File folder = new File(pathToFolder);

        if(folder.isDirectory()){
            // получаем только ТХТ файл согласно условию
            List<File> files = Arrays.stream(folder.listFiles((dir, name)->name.endsWith("txt")))
                    .limit(countToParse)
                    .collect(Collectors.toList());

            // проверка на то, что после фильтрации есть файлы, подходящие под наше условие
            if(files.size() == 0){
                System.out.println("Нет подходящих файлов");
                return;
            }

            for(File file: files){
                readFile(file);
            }
            int numberOfFiles = folder.listFiles().length;
            int inValidDoc = numberOfFiles - files.size();
            int validDoc = files.size();
            System.out.printf("Всего обработано файлов:" + numberOfFiles +  ",\n" +
            validDoc + " обработанных документов в формате txt, " + inValidDoc + " документ(а) невалидного формата");

        } else {
            System.out.println("Невалидный путь");
        }
    }

    private void readFile(File file){
        Pattern docPattern = Pattern.compile("\\d{4}[-][a-zа-я]{3}[-]\\d{4}[-][a-zа-я]{3}[-]\\d[a-zа-я]\\d[a-zа-я]", Pattern.CASE_INSENSITIVE);
        Pattern phonePattern = Pattern.compile("(\\+*)[(]\\d{2}[)]\\d{7}([\\W\\n\\t]|$)");
        Pattern emailPattern = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String docOneLine;

            // читаем документ посстрочно и анализируем
            while((docOneLine = reader.readLine()) != null){
                Matcher docMatcher = docPattern.matcher(docOneLine);
                Matcher phoneMatcher = phonePattern.matcher(docOneLine);
                Matcher emailMatcher = emailPattern.matcher(docOneLine);


                HashMap<String, String> mapDocInfo = new HashMap<>();
                // TODO рефактор
                if(docMatcher.find()){

                    mapDocInfo.put(file.getName().replaceAll(".txt", ""), docMatcher.group());
                  // System.out.println(docOneLine.substring(docMatcher.start(),docMatcher.end()));
                }

                if(phoneMatcher.find()){
                    mapDocInfo.put(file.getName().replaceAll(".txt", ""), phoneMatcher.group());
                   //System.out.println(docOneLine.substring(phoneMatcher.start(),phoneMatcher.end()));
                }

                if(emailMatcher.find()){
                    mapDocInfo.put(file.getName().replaceAll(".txt", ""), emailMatcher.group());
                  // System.out.println(docOneLine.substring(emailMatcher.start(),emailMatcher.end()));
                }
                Files.write(Paths.get("1.txt"),
                        mapDocInfo.entrySet().stream()
                                .map(k->k.getKey() + " - " + k.getValue())
                                .collect(Collectors.toList()));
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
   }
