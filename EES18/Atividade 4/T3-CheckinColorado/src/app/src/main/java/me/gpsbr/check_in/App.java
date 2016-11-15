package me.gpsbr.check_in;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.parse.Parse;
import com.parse.PushService;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Classe da aplicação.
 * Consiste em basicamente toda a lógica de login, checkin, checkuot e armazenamento de dados do
 * aplicativo.
 *
 * @author   Gustavo Seganfredo <gustavosf@gmail.com>
 * @since    1.0
 */
public class App extends Application {

    final public static String TAG = "Check-in";

    protected static Application app;
    protected static Context context;
    protected static SharedPreferences data;

    protected static ArrayList<Game> games = new ArrayList<Game>();
    protected static ArrayList<Card> cards = new ArrayList<Card>();

    protected static Set<String> parseSubscriptions;

    public static CheckinClient client;

    // Google Analytics
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    // ------------------------------------------------------------------------------------- //
    // - Métodos da Aplicação -------------------------------------------------------------- //
    // ------------------------------------------------------------------------------------- //

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = app.getApplicationContext();
        data = context.getSharedPreferences("data", MODE_PRIVATE);
        client = new CheckinClient(this);

        // Initializing Parse
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_app_key));
        PushService.setDefaultPushCallback(this, LoginActivity.class);

        // Garante a inscrição do user no canal "checkin"
        parseSubscriptions = PushService.getSubscriptions(this);
        parseSubscribe("checkin");
    }

    /**
     * Salva o estado do app.
     * Resumidamente, salva os jogos e os cartões, porque o resto ele regenera no onCreate
     *
     * @param savedInstanceState Instância do estado
     */
    public static void saveState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("games", games);
        savedInstanceState.putParcelableArrayList("cards", cards);
    }

    /**
     * Recupera o estado do app.
     * Resumidamente, recupera os jogos e os cartões, porque o resto ele regenera no onCreate
     *
     * @param savedInstanceState Instância do estado
     */
    public static void restoreState(Bundle savedInstanceState) {
        games = savedInstanceState.getParcelableArrayList("games");
        cards = savedInstanceState.getParcelableArrayList("cards");
    }

    /**
     * Inscreve o usuário em um canal do parse
     *
     * @param channel ID do canal para inscrever a pessoa
     */
    public static void parseSubscribe(String channel) {
        manageParseSubscription(channel, true);
    }

    /**
     * Desinscreve o usuário em um canal do parse
     *
     * @param channel ID do canal para desinscrever a pessoa
     */
    public static void parseUnsubscribe(String channel) {
        manageParseSubscription(channel, false);
    }

    /**
     * Gerencia inscrições em canais no parse
     *
     * @param channel ID do canal para inscrever a pessoa
     * @param tuneIn  true pra ligar a inscrição no canal, false para desligar
     */
    protected static void manageParseSubscription(String channel, Boolean tuneIn) {
        if (tuneIn && !parseSubscriptions.contains(channel)) {
            PushService.subscribe(App.app, channel, LoginActivity.class);
            if (channel == "checkin") {
                // Checkin só é setado uma vez, então se estiver sendo setado é porque é a primeira
                // vez que o usuário abre o celular. Assim, setamos "NOT_CHECKIN" também porque
                // a princípio ele não fez checkin. Após se logar, caso ele já tenha feito login
                // o app identifica e remove ele do canal
                PushService.subscribe(App.app, "NOT_CHECKIN", LoginActivity.class);
            }
            parseSubscriptions = PushService.getSubscriptions(App.app);
        }
        if (!tuneIn && parseSubscriptions.contains(channel)) {
            PushService.unsubscribe(App.app, channel);
            parseSubscriptions = PushService.getSubscriptions(App.app);
        }
    }

    /**
     * Tretas do Google Analytics
     *
     * @param trackerId ID da propriedade no analytics
     * @return          Tracker
     */
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker("UA-42184575-3")
                    : analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    /**
     * Proxy para exibir toasts no app
     *
     * @param text Texto a ser exibido no toast
     */
    public static void toaster(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Proxy para a recuperação de dados básico do app, usando key-value
     *
     * @param key Chave do dado a ser recuperado
     * @return    Dado
     */
    public static String data(String key) {
        return data.getString(key, "");
    }
    public static Set<String> dataSet(String key) {
        return data.getStringSet(key, new HashSet<String>());
    }

    /**
     * Proxy para o armazenameto de dados básico do app, usando key-value
     *
     * @param key   Chave do dado a ser inserido / editado
     * @param value Valor do dado
     * @return      true se o dado foi inserido corretamente, false do contrário
     */
    public static Boolean data(String key, String value) {
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    public static Boolean dataSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = data.edit();
        editor.putStringSet(key, value);
        return editor.commit();
    }

    /**
     * Verifica se usuário está logado ou não
     *
     * @return true se o usuário estiver logado, falso do contrário
     */
    public static Boolean isUserLoggedIn() {
        return !data("registration_number").equals("");
    }

    /**
     * Desloga um usuário
     */
    public static void logout() {
        data("registration_number", "");
        data("password", "");
        data("checkin_disabled", "");

        // Reseta os jogos e cartões
        games = new ArrayList<Game>();
        cards = new ArrayList<Card>();

        // Informa o usuário
        App.toaster(context.getString(R.string.logout));

        // Volta pra tela de login
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Loga um usuário
     * Simplesmente registra o número de matrícula e senha
     *
     * @param registration_number Número de matrícula
     * @param password            Senha
     */
    public static void login(String registration_number, String password) {
        // @TODO Mover toda a lógia de login do controller LoginActivity pra cá?
        data("registration_number", registration_number);
        data("password", password);
    }

    /**
     * Efetua o login do usuário utilizando as credenciais registradas em memória
     */
    public static void relogin(JsonHttpResponseHandler responseHandler) {
        String url = "?matricula="+App.data("registration_number")+"&senha="+App.data("password");
        App.client.get(url, null, responseHandler);
    }

    /**
     * Salva o recibo de check-in/out na memória do aparelho
     *
     * @param game Jogo para o qual foi feito o check*in/out
     * @param card Cartão onde foi feito o check-in/out
     * @param data Dados do check-in/out
     */
    public static void printReceipt(Game game, Card card, JSONObject data) {
        // Busca o template do comprovante nos resources
        Boolean in = data.optString("fila") == "";

        Resources res = App.context.getResources();
        int resId = in ? R.drawable.comprovante_checkin : R.drawable.comprovante_checkout;
        Bitmap bitmap = (BitmapFactory.decodeResource(res, resId))
                .copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);

        // Pinta os dados sobre o comprovante
        canvas.drawText(data.optString("sid"), 233, 511, paint);
        canvas.drawText("Inter X "+game.getAway(), 196, 540, paint);
        canvas.drawText(game.getDate(), 194, 570, paint);
        canvas.drawText(game.getVenue(), 219, 598, paint);
        if (!in) {
            canvas.drawText(data.optString("codigosetor"), 185, 689, paint);
            canvas.drawText(data.optString("fila"), 266, 689, paint);
            canvas.drawText(data.optString("nrlugar"), 357, 689, paint);
        }

        // Salva o bitmap :)
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Checkin/check"+(in?"in-":"out-")+card.getId()+"-"+game.getId()+".jpg");
        file.mkdirs();
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            // Disparado media scanner, porque por algum motivo não aparece o
            // comprovante na galeria
            MediaScannerConnection.scanFile(App.app,
                    new String[]{file.toString()}, null, null);
        } catch (Exception e) {
            // TODO: Tratar problema no salvamento do arquivo
            // e.printStackTrace();
        }
    }

    protected static class Scrapper {
        protected JSONObject json;

        public Scrapper(JSONObject json) {
            this.json = json;
        }

        public ArrayList<Game> getGames() {
            ArrayList<Game> games = new ArrayList<Game>();
            JSONObject gameList = json.optJSONObject("jogos");
            if (gameList == null) return games;
            Iterator<String> keys = gameList.keys();
            while (keys.hasNext()) {
                String id = keys.next();
                JSONObject gameInfo = gameList.optJSONObject(id);
                String away = gameInfo.optString("timevisitante");
                String venue = gameInfo.optString("estadio");
                String date = gameInfo.optString("data");
                String hour = gameInfo.optString("hora");
                String tournament = gameInfo.optString("campeonato");
                Game game = new Game(id, "Internacional", away, venue, date+' '+hour, tournament);
                game.enableCheckin(); // se aparece na listagem é porque está habilitado
                // TODO : adicionar setores
                games.add(game);
            }
            return games;
        }

        public ArrayList<Card> getCards() {
            ArrayList<Card> cards = new ArrayList<Card>();
            JSONObject cardList = json.optJSONObject("cartoes");
            Iterator<String> keys = cardList.keys();
            while (keys.hasNext()) {
                String id = keys.next();
                JSONObject cardInfo = cardList.optJSONObject(id);
                cards.add(new Card(
                        id,
                        cardInfo.optString("chave"),
                        cardInfo.optString("nome"),
                        cardInfo.optString("descricao")
                ));
            }
            return cards;
        }
    }

    /**
     * Cria uma lista de jogos fazendo scrape da página de checkin
     *
     * @param json JSON retornado pelo login
     */
    public static void buildCheckinFrom(JSONObject json) {
        Scrapper scrapper = new Scrapper(json);
        games = scrapper.getGames();
    }

    /**
     * Mostra a dialog de "sobre"
     */
    public static void showAbout(Context context) {
        android.app.Dialog dialog = App.Dialog.show(context, R.layout.dialog_about, "Sobre");

        String version = "?.0.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {}

        ((TextView)dialog.findViewById(R.id.link_policy))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)dialog.findViewById(R.id.link_fanpage))
                .setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)dialog.findViewById(R.id.about_version))
                .setText(context.getString(R.string.about_version, version));
    }

    /**
     * Retorna a lista de jogos
     * @return Lista de jogos
     */
    public static ArrayList<Game> getGameList() {
        return games;
    }

    /**
     * Retorna a lista de cartões
     * @return Lista de cartões
     */
    public static ArrayList<Card> getCards() { return cards; }

    /**
     * Busca por um jogo
     */
    public static Game getGame(int gameId) {
        return games.get(gameId);
    }

    /**
     * Busca por um cartão
     */
    public static Card getCard(int cardId) {
        return cards.get(cardId);
    }


    /**
     * Classe proxy para dialogs do android
     *
     * @author  Gustavo Seganfredo
     * @since   1.0
     */
    public static class Dialog {

        protected static ProgressDialog progressDialog;
        protected static AlertDialog alertDialog;

        /**
         * Mostra dialog de progresso com mensagem
         *
         * @param context Contexto da atividade pai
         * @param message Mensagem a ser exibida
         * @return        Objeto ProgressDialog
         */
        public static ProgressDialog showProgress(Context context, String message) {
            progressDialog = ProgressDialog.show(context, "", message, true);
            return progressDialog;
        }

        /**
         * Desaparece com a dialog de progresso
         */
        public static void dismissProgress() {
            progressDialog.dismiss();
        }

        /**
         * Exibe uma mensagem de alerta
         *
         * @param context  Contexto da atividade pai
         * @param message  Mensagem da janela
         * @param okText   Texto do botão OK
         * @param callback Callback a ser executada depois de clicar no botão ok
         * @return         Objeto AlertDialog
         */
        public static AlertDialog showAlert(Context context, String message, String title,
                                            String okText,
                                            DialogInterface.OnClickListener callback) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setCancelable(false);
            if (title != null) builder.setTitle(title);
            builder.setPositiveButton(okText, callback);
            alertDialog = builder.create();
            alertDialog.show();
            return alertDialog;
        }
        public static AlertDialog showAlert(Context context, String message) {
            return showAlert(context, message, null, "OK");
        }
        public static AlertDialog showAlert(Context context, String message, String title) {
            return showAlert(context, message, title, "OK");
        }
        public static AlertDialog showAlert(Context context, String message, String title,
                                            String okText) {
            return showAlert(context, message, title, okText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }

        // Mesma coisa só que com content-view
        public static android.app.Dialog show(Context context, int contentView, String title) {
            android.app.Dialog dialog = new android.app.Dialog(context);
            dialog.setContentView(contentView);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setTitle(title);
            dialog.show();
            return dialog;
        }
    }


    /**
     * Classe de utilidades variadas não-diretamente relacionadas ao app
     *
     * @author  Gustavo Seganfredo
     * @since   1.0
     */
    public static class Utils {

        /**
         * Retorna uma frase com todas as palavras capitalizadas
         * @param string Frase a ser capitalizada
         * @return       Frase capitalizada
         */
        public static String capitalizeWords(String string) {
            char[] chars = string.toLowerCase().toCharArray();
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
                if (!found && Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    found = true;
                } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                    found = false;
                }
            }
            return String.valueOf(chars);
        }
    }
}


class CheckinClient {
    final String BASE_URL = "http://www.internacional.com.br/checkin/public/";
    long lastRequest;
    private static AsyncHttpClient client = new AsyncHttpClient();

    CheckinClient(Application app) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(app);
        client.setCookieStore(myCookieStore);
    }

    // ------------------------------------------------------------------------------------- //
    // - Métodos do client ----------------------------------------------------------------- //
    // ------------------------------------------------------------------------------------- //

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        lastRequest = System.currentTimeMillis() / 1000L;
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public  void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        lastRequest = System.currentTimeMillis() / 1000L;
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    /**
     * Verifica se a conexão já deu timeout no servidor
     * @return true Se der pra considerar que a sessão no servidor expirou (timeout de 5 mins)
     */
    public boolean timeout() {
        long delay = (System.currentTimeMillis() / 1000L) - lastRequest;
        return delay > 60*5;
    }

    // ------------------------------------------------------------------------------------- //
    // - Métodos da API -------------------------------------------------------------------- //
    // ------------------------------------------------------------------------------------- //

 }