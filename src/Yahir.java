import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Yahir {

    // Generador automático de archivos optimizado
    private static final int TOTAL_FILES = 1_000_000;
    private static final int TARGET_SIZE_MB = 5000;
    private static final int TARGET_SIZE_BYTES = TARGET_SIZE_MB * 1024 * 1024;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors(); // Usar todos los cores
    private static final AtomicInteger filesCompleted = new AtomicInteger(0);

    // Pre-calcular el contenido para reutilizar
    private static final String FILLER_CONTENT = generateFillerContent();

    public static void generateFiles() {
        // Obtener la ruta del escritorio del usuario
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + java.io.File.separator + "Desktop" + java.io.File.separator + "generated_files";

        // Crear directorio para los archivos en el escritorio
        try {
            Files.createDirectories(Paths.get(desktopPath));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
            return;
        }

        System.out.println("=== GENERADOR DE ARCHIVOS OPTIMIZADO ===");
        System.out.println("Generando archivos en: " + desktopPath);
        System.out.println("Archivos a generar: " + String.format("%,d", TOTAL_FILES));
        System.out.println("Tamaño por archivo: " + TARGET_SIZE_MB + " MB");
        System.out.println("Tamaño total estimado: " + String.format("%,d", (TOTAL_FILES * TARGET_SIZE_MB / 1024)) + " GB");
        System.out.println("Threads utilizados: " + THREAD_COUNT);
        System.out.println("========================================");

        long startTime = System.currentTimeMillis();

        // Hilo para mostrar progreso
        Thread progressThread = new Thread(() -> {
            while (filesCompleted.get() < TOTAL_FILES) {
                try {
                    Thread.sleep(2000); // Actualizar cada 2 segundos
                    int completed = filesCompleted.get();
                    if (completed > 0) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        double progress = (double) completed / TOTAL_FILES * 100;
                        double rate = completed / (elapsed / 1000.0); // archivos por segundo
                        long remaining = (long) ((TOTAL_FILES - completed) / rate);

                        System.out.printf("✓ Progreso: %.2f%% (%,d/%,d) | %.1f archivos/seg | ETA: %02d:%02d:%02d%n",
                                progress, completed, TOTAL_FILES, rate,
                                remaining / 3600, (remaining % 3600) / 60, remaining % 60);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        // Usar ExecutorService para paralelización
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        progressThread.start();

        // Generar archivos en paralelo
        for (int i = 1; i <= TOTAL_FILES; i++) {
            final int fileNumber = i;
            executor.submit(() -> {
                try {
                    generateSingleFileOptimized(fileNumber, desktopPath);
                    filesCompleted.incrementAndGet();
                } catch (IOException e) {
                    System.err.println("Error generando archivo " + fileNumber + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        progressThread.interrupt();

        long totalTime = System.currentTimeMillis() - startTime;
        double avgRate = TOTAL_FILES / (totalTime / 1000.0);

        System.out.println("\n🎉 ¡GENERACIÓN COMPLETADA! 🎉");
        System.out.printf("⏱️  Tiempo total: %.2f segundos (%.2f minutos)%n",
                totalTime / 1000.0, totalTime / 60000.0);
        System.out.printf("⚡ Velocidad promedio: %.1f archivos/segundo%n", avgRate);
        System.out.printf("💾 Datos generados: %,d MB (%,d GB)%n",
                TOTAL_FILES * TARGET_SIZE_MB, TOTAL_FILES * TARGET_SIZE_MB / 1024);
    }

    private static String generateFillerContent() {
        StringBuilder sb = new StringBuilder();

        // Calcular contenido base
        String baseContent = "public class Yahir {\n}\n";
        int baseSize = baseContent.getBytes().length;
        int remainingBytes = TARGET_SIZE_BYTES - baseSize;

        // Crear línea de comentario optimizada
        String commentLine = "    // Contenido generado automáticamente para alcanzar exactamente " + TARGET_SIZE_MB + "MB - ";
        String padding = "x".repeat(100); // 100 x's por línea
        commentLine += padding + "\n";

        int commentLineSize = commentLine.getBytes().length;
        int linesNeeded = remainingBytes / commentLineSize;

        // Pre-generar todo el contenido de relleno
        for (int i = 0; i < linesNeeded; i++) {
            sb.append(commentLine);
        }

        // Ajustar bytes restantes
        int bytesWritten = baseSize + (linesNeeded * commentLineSize);
        int finalAdjustment = TARGET_SIZE_BYTES - bytesWritten - 2; // -2 para }\n

        if (finalAdjustment > 0) {
            sb.append("    // ");
            sb.append("x".repeat(Math.max(0, finalAdjustment - 7))); // -7 para "// " y "\n"
            sb.append("\n");
        }

        return sb.toString();
    }

    private static void generateSingleFileOptimized(int fileNumber, String desktopPath) throws IOException {
        String fileName = desktopPath + java.io.File.separator + "Yahir_" + fileNumber + ".java";

        // Usar BufferedWriter para mejor rendimiento
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName), 8192)) {
            writer.write("public class Yahir {\n");
            writer.write(FILLER_CONTENT); // Contenido pre-generado
            writer.write("}\n");
        }
    }

    // Método principal para ejecutar la generación
    public static void main(String[] args) {
        System.out.println("Iniciando generador optimizado...");
        generateFiles();
    }
}