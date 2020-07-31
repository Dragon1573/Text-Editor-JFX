/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Created by: Dragon1573
 * Last Modified: 2020/07/31 23:07:49 CST
 */

package wang.dragon1573.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import info.monitorenter.cpdetector.io.*;
import wang.dragon1573.Status;

public class FileUtils {
    /**
     * <h2>BOM标记</h2>
     */
    private static final byte[] BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
    /**
     * <h2>默认空文件名</h2>
     */
    public static final String EMPTY_NAME = "无标题";

    /**
     * <h2>行尾枚举类</h2>
     */
    public enum SeparatorTypes {
        /** Windows标准行尾格式 */
        CRLF("CRLF - Windows (\\r\\n)"),
        /** Unix与macOS标准行尾格式 */
        LF("LF - Unix & macOS (\\n)"),
        /** 传统macOS标准行尾格式 */
        CR("CR - Classic macOS (\\r)");
        private final String string;

        @Override
        public String toString() {
            return this.string;
        }

        SeparatorTypes(final String string) {
            this.string = string;
        }
    }

    /**
     * <h2>检测文件编码格式</h2>
     *
     * @param bytes
     *     字节数组
     */
    private static String detectEncoding(final byte[] bytes) {
        /* 创建识别器 */
        final CodepageDetectorProxy proxy = CodepageDetectorProxy.getInstance();
        proxy.add(new ParsingDetector(false));
        proxy.add(JChardetFacade.getInstance());
        proxy.add(ASCIIDetector.getInstance());
        proxy.add(UnicodeDetector.getInstance());

        /* 读取字节数组流，分析文件编码 */
        try (final ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            // 获得文件使用的字符集
            final Charset charset = proxy.detectCodepage(stream, bytes.length);
            // 返回解析后的字符串
            return new String(bytes, charset);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        /* 无法解析的文件，返回空字符串 */
        return "";
    }

    /**
     * <h2>打开文件</h2>
     */
    public static String openFile() {
        // 新建文件选择器（文件/目录是2个不同的类）
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("打开自...");
        chooser.setInitialDirectory(new File("."));
        chooser.getExtensionFilters().addAll(
            new ExtensionFilter("纯文本文件", "*.txt"),
            new ExtensionFilter("Markdown标记语言", "*.md"),
            new ExtensionFilter("CSV逗号分隔文件", "*.csv"),
            new ExtensionFilter("所有文件", "*.*")
        );

        /* 弹出对话框 */
        final File file = chooser.showOpenDialog(null);
        if (file != null) {
            /* 更新文件名标签 */
            Status.fileName.set(file.getName());
            Status.currentFile = file;

            /* 文件解析 */
            try (final FileInputStream inputStream = new FileInputStream(file)) {
                /* Java9以上特性 */
                byte[] bytes = inputStream.readAllBytes();

                /* 人工剔除BOM标记 */
                if (Arrays.equals(bytes, 0, BOM.length, BOM, 0, BOM.length)) {
                    bytes = Arrays.copyOfRange(bytes, 3, bytes.length);
                }

                /* 使用第三方工具识别文件编码格式 */
                final String content = detectEncoding(bytes);

                /* 行尾标准化 */
                return tailFormat(content, "\n");
            } catch (final FileNotFoundException e) {
                System.err.println("[ERROR] 文件未找到！");
                e.printStackTrace();
            } catch (final IOException e) {
                System.err.println("[ERROR] I/O异常！");
                e.printStackTrace();
            }
        }

        /* 文件打开异常，返回空指针 */
        return null;
    }

    /**
     * <h2>保存文件</h2>
     *
     * @param content
     *     文本内容
     *
     * @return 文件是否被正确写入磁盘
     */
    public static boolean saveFile(final String content) {
        if (Status.currentFile == null) {
            /* 尚未保存的新文件 */

            /* 新建文件选择器 */
            final FileChooser chooser = new FileChooser();
            chooser.setTitle("另存为...");
            // 指定默认文件名
            chooser.setInitialFileName("新建文本文件.txt");
            // 指定默认目录（当前目录）
            chooser.setInitialDirectory(new File("."));
            // 指定文件后缀名过滤器
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("纯文本文件", "*.txt"),
                new FileChooser.ExtensionFilter("Markdown标记语言", "*.md"),
                new FileChooser.ExtensionFilter("CSV逗号分隔文件", "*.csv"),
                new FileChooser.ExtensionFilter("所有文件", "*.*")
            );
            // 弹出会话框，接收用户选项
            final File targetFile = chooser.showSaveDialog(null);

            if (targetFile != null) {
                // 写入文件
                FileUtils.writeFile(targetFile, content);
                // 更新文件名
                Status.fileName.setValue(targetFile.getName());
                Status.currentFile = targetFile;
                // 更新状态标记
                Status.isUnsaved = false;
                return true;
            }
            return false;
        } else {
            /* 未保存的本地文件*/

            FileUtils.writeFile(Status.currentFile, content);
            Status.isUnsaved = false;

            return true;
        }
    }

    /**
     * <h2>行尾格式化（指定行尾）</h2>
     * <p>&emsp;&emsp;根据参数指定的行尾进行格式化。</p>
     *
     * @param content
     *     文本内容
     * @param separator
     *     行尾换行符
     */
    public static String tailFormat(final String content, final String separator) {
        // 获取系统的标准行尾模式
        return switch (separator) {
            // Windows设备
            case "\r\n" -> content.replaceAll("\r", "\n")
                                  .replaceAll("\n{1,2}", "\r\n");

            // Linux和macOS设备
            case "\n" -> content.replaceAll("\r\n", "\n")
                                .replaceAll("\r", "\n");

            // 传统macOS设备
            case "\r" -> content.replaceAll("\r\n", "\r")
                                .replaceAll("\n", "\r");
            default -> content;
        };
    }

    /**
     * <h2>行尾格式化（用户自定义行尾）</h2>
     * <p>&emsp;&emsp;获取用户选定的行尾格式进行格式化。</p>
     *
     * @param content
     *     文本内容
     */
    public static String tailFormat(final String content) {
        return tailFormat(
            content,
            Status.LS_MAPPER.getKey(Status.lineSeparator.getValueSafe())
        );
    }

    /**
     * <h2>写入文件</h2>
     *
     * @param target
     *     目标文件
     * @param content
     *     文本内容
     */
    public static void writeFile(final File target, final String content) {
        /* 使用统一的格式规范写入文件 */
        try (final PrintStream stream = new PrintStream(target, StandardCharsets.UTF_8)) {
            stream.print(FileUtils.tailFormat(content));
            stream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
