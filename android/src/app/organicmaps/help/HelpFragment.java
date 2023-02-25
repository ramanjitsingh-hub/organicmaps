package app.organicmaps.help;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.organicmaps.BuildConfig;
import app.organicmaps.Framework;
import app.organicmaps.R;
import app.organicmaps.base.BaseMwmFragment;
import app.organicmaps.util.Config;
import app.organicmaps.util.Constants;
import app.organicmaps.util.DateUtils;
import app.organicmaps.util.Graphics;
import app.organicmaps.util.Utils;

public class HelpFragment extends BaseMwmFragment implements View.OnClickListener
{
  private String mDonateUrl;

  private TextView setupItem(@IdRes int id, boolean tint, @NonNull View frame)
  {
    final TextView view = frame.findViewById(id);
    view.setOnClickListener(this);
    if (tint)
      Graphics.tint(view);
    return view;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
  {
    mDonateUrl = Config.getDonateUrl();
    if (TextUtils.isEmpty(mDonateUrl) && !BuildConfig.FLAVOR.equals("google") && !BuildConfig.FLAVOR.equals("huawei"))
      mDonateUrl = getResources().getString(R.string.translated_om_site_url) + "donate/";

    View root = inflater.inflate(R.layout.about, container, false);

    ((TextView) root.findViewById(R.id.version))
        .setText(BuildConfig.VERSION_NAME);

    final boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

    final String dataVersion = DateUtils.getShortDateFormatter().format(Framework.getDataVersion());
    final TextView dataVersionView = (TextView) root.findViewById(R.id.data_version);
    if (dataVersionView != null)
      dataVersionView.setText(getString(R.string.data_version, dataVersion));
    final TextView osmPresentationView = (TextView) root.findViewById(R.id.osm_presentation);
    if (osmPresentationView != null)
      osmPresentationView.setText(getString(R.string.osm_presentation, dataVersion));

    setupItem(R.id.news, true, root);
    setupItem(R.id.web, true, root);
    setupItem(R.id.email, true, root);
    setupItem(R.id.github, false, root);
    setupItem(R.id.telegram, false, root);
    setupItem(R.id.instagram, false, root);
    setupItem(R.id.facebook, false, root);
    setupItem(R.id.twitter, false, root);
    setupItem(R.id.matrix, true, root);
    setupItem(R.id.mastodon, false, root);
    setupItem(R.id.openstreetmap, true, root);
    setupItem(R.id.faq, true, root);
    setupItem(R.id.report, isLandscape, root);

    final TextView supportUsView = root.findViewById(R.id.support_us);
    if (BuildConfig.FLAVOR.equals("google") && !TextUtils.isEmpty(mDonateUrl))
      supportUsView.setVisibility(View.GONE);
    else
      setupItem(R.id.support_us, true, root);

    final TextView donateView = root.findViewById(R.id.donate);
    if (TextUtils.isEmpty(mDonateUrl))
      donateView.setVisibility(View.GONE);
    else
      setupItem(R.id.donate, isLandscape, root);

    if (BuildConfig.REVIEW_URL.isEmpty())
      root.findViewById(R.id.rate).setVisibility(View.GONE);
    else
      setupItem(R.id.rate, true, root);

    setupItem(R.id.copyright, false, root);
    View termOfUseView = root.findViewById(R.id.term_of_use_link);
    View privacyPolicyView = root.findViewById(R.id.privacy_policy);
    termOfUseView.setOnClickListener(v -> onTermOfUseClick());
    privacyPolicyView.setOnClickListener(v -> onPrivacyPolicyClick());

    return root;
  }

  private void openLink(@NonNull String link)
  {
    Utils.openUrl(requireActivity(), link);
  }

  private void onPrivacyPolicyClick()
  {
    openLink(getResources().getString(R.string.translated_om_site_url) + "privacy/");
  }

  private void onTermOfUseClick()
  {
    openLink(getResources().getString(R.string.translated_om_site_url) + "terms/");
  }

  @Override
  public void onClick(View v)
  {
    final int id = v.getId();
    if (id == R.id.web)
      openLink(getResources().getString(R.string.translated_om_site_url));
    else if (id == R.id.news)
      openLink(getResources().getString(R.string.translated_om_site_url) + "news/");
    else if (id == R.id.email)
      Utils.sendTo(requireContext(), BuildConfig.SUPPORT_MAIL, "Organic Maps");
    else if (id == R.id.github)
      openLink(Constants.Url.GITHUB);
    else if (id == R.id.telegram)
      openLink(getString(R.string.telegram_url));
    else if (id == R.id.instagram)
      openLink(getString(R.string.instagram_url));
    else if (id == R.id.facebook)
      Utils.showFacebookPage(requireActivity());
    else if (id == R.id.twitter)
      openLink(Constants.Url.TWITTER);
    else if (id == R.id.matrix)
      openLink(Constants.Url.MATRIX);
    else if (id == R.id.mastodon)
      openLink(Constants.Url.MASTODON);
    else if (id == R.id.openstreetmap)
      openLink(Constants.Url.OSM_ABOUT);
    else if (id == R.id.faq)
      ((HelpActivity) requireActivity()).stackFragment(FaqFragment.class, getString(R.string.faq), null);
    else if (id == R.id.report)
      Utils.sendBugReport(requireActivity(), "");
    else if (id == R.id.support_us)
      openLink(getResources().getString(R.string.translated_om_site_url) + "support-us/");
    else if (id == R.id.donate)
      openLink(mDonateUrl);
    else if (id == R.id.rate)
      Utils.openAppInMarket(requireActivity(), BuildConfig.REVIEW_URL);
    else if (id == R.id.copyright)
      ((HelpActivity) requireActivity()).stackFragment(CopyrightFragment.class, getString(R.string.copyright), null);
  }
}
