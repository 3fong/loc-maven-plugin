package com.ll;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal 项目下的文件数和代码行数统计
 *
 * @goal count
 */
public class CountMojo extends AbstractMojo {

    private static final String[] INCLUDES_DEFAULT = {"java","xml","properties"};

    /**
     * @parameter property = "project.basedir"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * @parameter property = "project.build.sourceDirectory"
     * @required
     * @readonly
     */
    private File sourceDirectory;

    /**
     * @parameter property = "project.build.testSourceDirectory"
     * @required
     * @readonly
     */
    private File testSourceDirectory;

    /**
     * @parameter property = "project.build.resources"
     * @required
     * @readonly
     */
    private List<Resource> resources;

    /**
     * @parameter property = "project.build.testResources"
     * @required
     * @readonly
     */
    private List<Resource> testResources;

    /**
     * 需要计数的文件类型
     * @ parameter
     */
    private String[] includes;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(includes == null || includes.length==0){
            includes = INCLUDES_DEFAULT;
        }

        try {
            countDir(sourceDirectory);
            countDir(testSourceDirectory);

            for (Resource r : resources) {
                countDir(new File(r.getDirectory()));
            }

            for (Resource r : testResources) {
                countDir(new File(r.getDirectory()));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("fail to count lines of code",e);
        }

    }

    private void countDir(File file) throws IOException {
        if(!file.exists()) {
            return;
        }

        List<File> collected = new ArrayList<File>();
        collectFiles(collected,file);
        int lines = 0;

        for (File f : collected) {
            lines += countLines(f);
        }

        String path = file.getAbsolutePath().substring(basedir.getAbsolutePath().length());

        getLog().info(path + ": files size: " + collected.size() + " lines num: "+lines);
    }

    private int countLines(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        int line = 0;
        try {
            while(reader.ready()){
                reader.readLine();
                line ++;
            }
        } finally {
            reader.close();
        }
        return line;
    }

    /**
     * 获取文件数据集
     * @param collected
     * @param file
     */
    private void collectFiles(List<File> collected, File file) {
        if(file.isFile()) {
            for (String include : includes) {
                if(file.getName().endsWith("."+include)) {
                    collected.add(file);
                    break;
                }
            }
        } else {
            for (File sub : file.listFiles()) {
                collectFiles(collected,sub);
            }
        }
    }
}
