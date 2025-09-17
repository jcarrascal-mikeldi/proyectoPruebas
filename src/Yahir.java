public class Yahir {

    // Generador automático de archivos de 50MB
    private static final int TOTAL_FILES = 1_000_000;
    private static final int TARGET_SIZE_MB = 50;
    private static final int TARGET_SIZE_BYTES = TARGET_SIZE_MB * 1024 * 1024;

    public static void generateFiles() {
        // Obtener la ruta del escritorio del usuario
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + java.io.File.separator + "Desktop" + java.io.File.separator + "generated_files";

        // Crear directorio para los archivos en el escritorio
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(desktopPath));
        } catch (java.io.IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
            return;
        }

        System.out.println("Generando archivos en: " + desktopPath);
        System.out.println("Iniciando generación de " + TOTAL_FILES + " archivos de " + TARGET_SIZE_MB + "MB cada uno...");
        System.out.println("Tamaño total estimado: " + (TOTAL_FILES * TARGET_SIZE_MB / 1024) + " GB");

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_FILES; i++) {
            try {
                generateSingleFile(i, desktopPath);

                // Mostrar progreso cada 1000 archivos
                if (i % 1000 == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    double progress = (double) i / TOTAL_FILES * 100;
                    System.out.printf("Progreso: %.2f%% (%d/%d archivos) - Tiempo: %.2f segundos%n",
                            progress, i, TOTAL_FILES, elapsed / 1000.0);
                }

            } catch (java.io.IOException e) {
                System.err.println("Error generando archivo " + i + ": " + e.getMessage());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("¡Generación completada!");
        System.out.printf("Tiempo total: %.2f segundos (%.2f minutos)%n",
                totalTime / 1000.0, totalTime / 60000.0);
    }

    private static void generateSingleFile(int fileNumber, String desktopPath) throws java.io.IOException {
        String fileName = desktopPath + java.io.File.separator + "Yahir_" + fileNumber + ".java";

        try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
            // Escribir la clase base
            writer.write("public class Yahir {\n");

            // Calcular cuántos comentarios necesitamos para llegar a 50MB
            String baseContent = "public class Yahir {\n}\n";
            int baseSize = baseContent.getBytes().length;
            int remainingBytes = TARGET_SIZE_BYTES - baseSize;

            // Generar comentarios largos para llenar el espacio
            String commentLine = "    // Este es un comentario muy largo para llenar espacio en el archivo y alcanzar exactamente 50MB de tamaño total del archivo Java generado automáticamente\n";
            int commentLineSize = commentLine.getBytes().length;
            int linesNeeded = remainingBytes / commentLineSize;

            // Escribir los comentarios dentro de la clase
            for (int i = 0; i < linesNeeded; i++) {
                writer.write(commentLine);
            }

            // Ajustar los bytes restantes si es necesario
            int bytesWritten = baseSize + (linesNeeded * commentLineSize);
            int finalAdjustment = TARGET_SIZE_BYTES - bytesWritten - 2; // -2 for closing brace and newline

            if (finalAdjustment > 0) {
                writer.write("    // ");
                for (int i = 0; i < finalAdjustment - 7; i++) { // -7 for "// " and "\n"
                    writer.write("x");
                }
                writer.write("\n");
            }

            writer.write("}\n");
        }
    }

    // Método principal para ejecutar la generación
    public static void main(String[] args) {
        generateFiles();
    }
}