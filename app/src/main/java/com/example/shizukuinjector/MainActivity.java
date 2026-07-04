package com.example.shizukuinjector;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ShizukuInjector";
    private static final int SHIZUKU_REQUEST_CODE = 1;

    // Destino fixo: pasta de dados do Free Fire
    private static final String TARGET_PATH =
            "/storage/emulated/0/Android/data/com.dts.freefireth/files/localconfig.json";

    // Conteúdo do arquivo a ser injetado
    private static final String JSON_CONTENT =
            "{\n  \"verAddr\": \"https://criticalxr-neck-head-config.onrender.com/\"\n}";

    private TextView tvStatus;
    // private TextView tvTarget;
    private Button btnRequestPermission;
    private Button btnInject;
    private TextView tvLog;

    private IFileService fileService;

    // ---------------------------------------------------------------
    // Listeners do Shizuku
    // ---------------------------------------------------------------

    private final Shizuku.OnBinderReceivedListener binderReceivedListener = () ->
            runOnUiThread(() -> {
                log("Shizuku binder recebido");
                checkShizukuStatus();
            });

    private final Shizuku.OnBinderDeadListener binderDeadListener = () ->
            runOnUiThread(() -> {
                log("Shizuku binder perdido");
                fileService = null;
                updateUI(false);
            });

    private final Shizuku.OnRequestPermissionResultListener permissionListener =
            this::onShizukuPermissionResult;

    // ---------------------------------------------------------------
    // Conexão com o UserService
    // ---------------------------------------------------------------

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            fileService = IFileService.Stub.asInterface(binder);
            runOnUiThread(() -> {
                log("UserService conectado com sucesso");
                btnInject.setEnabled(true);
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            fileService = null;
            runOnUiThread(() -> {
                log("UserService desconectado");
                btnInject.setEnabled(false);
            });
        }
    };

    // ---------------------------------------------------------------
    // Ciclo de vida da Activity
    // ---------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus            = findViewById(R.id.tvStatus);
        // tvTarget            = findViewById(R.id.tvTarget);
        btnRequestPermission = findViewById(R.id.btnRequestPermission);
        btnInject           = findViewById(R.id.btnInject);
        tvLog               = findViewById(R.id.tvLog);

        // tvTarget.setText("Destino: " + TARGET_PATH);

        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener);
        Shizuku.addBinderDeadListener(binderDeadListener);
        Shizuku.addRequestPermissionResultListener(permissionListener);

        btnRequestPermission.setOnClickListener(v -> requestShizukuPermission());
        btnInject.setOnClickListener(v -> injectFile());

        checkShizukuStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeBinderReceivedListener(binderReceivedListener);
        Shizuku.removeBinderDeadListener(binderDeadListener);
        Shizuku.removeRequestPermissionResultListener(permissionListener);

        if (fileService != null) {
            try {
                fileService.destroy();
            } catch (RemoteException ignored) {}
        }

        try {
            Shizuku.unbindUserService(buildUserServiceArgs(), serviceConnection, true);
        } catch (Exception ignored) {}
    }

    // ---------------------------------------------------------------
    // Lógica do Shizuku
    // ---------------------------------------------------------------

    private void checkShizukuStatus() {
        if (!Shizuku.pingBinder()) {
            log("Shizuku não está rodando. Abra o app Shizuku e inicie o serviço.");
            updateUI(false);
            return;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            log("Shizuku: permissão já concedida");
            updateUI(true);
            bindUserService();
        } else {
            log("Shizuku: aguardando permissão");
            updateUI(false);
        }
    }

    private void requestShizukuPermission() {
        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, "Shizuku não está rodando!", Toast.LENGTH_LONG).show();
            return;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            log("Permissão já concedida");
            updateUI(true);
            bindUserService();
            return;
        }

        if (Shizuku.shouldShowRequestPermissionRationale()) {
            Toast.makeText(this,
                    "Permissão negada anteriormente. Abra o app Shizuku e conceda manualmente.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        log("Solicitando permissão ao Shizuku...");
        Shizuku.requestPermission(SHIZUKU_REQUEST_CODE);
    }

    private void onShizukuPermissionResult(int requestCode, int grantResult) {
        if (requestCode == SHIZUKU_REQUEST_CODE) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                log("Permissão concedida!");
                updateUI(true);
                bindUserService();
            } else {
                log("Permissão negada.");
                updateUI(false);
            }
        }
    }

    private void bindUserService() {
        if (fileService != null) return;
        log("Iniciando UserService com privilégio de shell...");
        Shizuku.bindUserService(buildUserServiceArgs(), serviceConnection);
    }

    private Shizuku.UserServiceArgs buildUserServiceArgs() {
        return new Shizuku.UserServiceArgs(
                new ComponentName(getPackageName(), FileUserService.class.getName()))
                .daemon(false)
                .processNameSuffix("file_service")
                .debuggable(BuildConfig.DEBUG)
                .version(BuildConfig.VERSION_CODE);
    }

    // ---------------------------------------------------------------
    // Injeção do arquivo
    // ---------------------------------------------------------------

    private void injectFile() {
        if (fileService == null) {
            Toast.makeText(this, "Serviço não conectado. Aguarde ou solicite permissão.", Toast.LENGTH_SHORT).show();
            return;
        }

        log("Injetando arquivo em:\n" + TARGET_PATH);

        new Thread(() -> {
            try {
                boolean success = fileService.writeToFile(TARGET_PATH, JSON_CONTENT);
                runOnUiThread(() -> {
                    if (success) {
                        log("✔ SUCESSO! localconfig.json injetado.");
                        Toast.makeText(this, "Arquivo injetado com sucesso!", Toast.LENGTH_LONG).show();
                    } else {
                        log("✘ FALHA ao injetar. Verifique se o Free Fire está instalado.");
                        Toast.makeText(this, "Falha na injeção.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (RemoteException e) {
                runOnUiThread(() -> {
                    log("ERRO IPC: " + e.getMessage());
                    Toast.makeText(this, "Erro de comunicação com o serviço.", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // ---------------------------------------------------------------
    // Helpers de UI
    // ---------------------------------------------------------------

    private void updateUI(boolean shizukuGranted) {
        if (shizukuGranted) {
            tvStatus.setText("● Shizuku: Conectado e Autorizado");
            tvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            btnRequestPermission.setEnabled(false);
        } else {
            tvStatus.setText("● Shizuku: Aguardando permissão");
            tvStatus.setTextColor(getColor(android.R.color.holo_red_dark));
            btnRequestPermission.setEnabled(true);
            btnInject.setEnabled(false);
        }
    }

    private void log(String message) {
        Log.i(TAG, message);
        runOnUiThread(() -> tvLog.append("\n" + message));
    }
}
