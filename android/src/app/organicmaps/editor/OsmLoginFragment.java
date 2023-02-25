package app.organicmaps.editor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.appcompat.app.AlertDialog;

import app.organicmaps.Framework;
import app.organicmaps.R;
import app.organicmaps.base.BaseMwmToolbarFragment;
import app.organicmaps.util.Constants;
import app.organicmaps.util.DateUtils;
import app.organicmaps.util.InputUtils;
import app.organicmaps.util.UiUtils;
import app.organicmaps.util.concurrency.ThreadPool;
import app.organicmaps.util.concurrency.UiThread;

public class OsmLoginFragment extends BaseMwmToolbarFragment
{
  private ProgressBar mProgress;
  private Button mLoginButton;
  private Button mLostPasswordButton;
  private EditText mLoginInput;
  private EditText mPasswordInput;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
  {
    return inflater.inflate(R.layout.fragment_osm_login, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    getToolbarController().setTitle(R.string.login);
    mLoginInput = view.findViewById(R.id.osm_username);
    mPasswordInput = view.findViewById(R.id.osm_password);
    mLoginButton = view.findViewById(R.id.login);
    mLoginButton.setOnClickListener((v) -> login());
    mLostPasswordButton = view.findViewById(R.id.lost_password);
    mLostPasswordButton.setOnClickListener((v) -> recoverPassword());
    Button registerButton = view.findViewById(R.id.register);
    registerButton.setOnClickListener((v) -> register());
    mProgress = view.findViewById(R.id.osm_login_progress);
    final String dataVersion = DateUtils.getShortDateFormatter().format(Framework.getDataVersion());
    ((TextView) view.findViewById(R.id.osm_presentation))
        .setText(getString(R.string.osm_presentation, dataVersion));
  }

  private void login()
  {
    InputUtils.hideKeyboard(mLoginInput);
    final String username = mLoginInput.getText().toString();
    final String password = mPasswordInput.getText().toString();
    enableInput(false);
    UiUtils.show(mProgress);
    mLoginButton.setText("");

    ThreadPool.getWorker().execute(() -> {
      final String[] auth = OsmOAuth.nativeAuthWithPassword(username, password);
      final String username1 = auth == null ? null : OsmOAuth.nativeGetOsmUsername(auth[0], auth[1]);
      UiThread.run(() -> processAuth(auth, username1));
    });
  }

  private void enableInput(boolean enable)
  {
    mPasswordInput.setEnabled(enable);
    mLoginInput.setEnabled(enable);
    mLoginButton.setEnabled(enable);
    mLostPasswordButton.setEnabled(enable);
  }

  private void recoverPassword()
  {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Url.OSM_RECOVER_PASSWORD)));
  }

  private void processAuth(@Size(2) String[] auth, String username)
  {
    if (!isAdded())
      return;

    enableInput(true);
    UiUtils.hide(mProgress);
    mLoginButton.setText(R.string.login_osm);
    if (auth == null)
      onAuthFail();
    else
      onAuthSuccess(auth, username);
  }

  private void onAuthFail()
  {
    new AlertDialog.Builder(requireActivity(), R.style.MwmTheme_AlertDialog)
        .setTitle(R.string.editor_login_error_dialog)
        .setPositiveButton(R.string.ok, null)
        .show();
  }

  private void onAuthSuccess(@Size(2) String[] auth, String username)
  {
    OsmOAuth.setAuthorization(requireContext(), auth[0], auth[1], username);
    final Bundle extras = requireActivity().getIntent().getExtras();
    if (extras != null && extras.getBoolean("redirectToProfile", false))
      startActivity(new Intent(requireContext(), ProfileActivity.class));
    requireActivity().finish();
  }

  private void register()
  {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Url.OSM_REGISTER)));
  }
}
