using Android.App;
using Android.OS;
using Android.Support.V7.App;
using Android.Runtime;
using Android.Widget;
using Xamarin.Essentials;

namespace App1
{
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_main);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            TextView garo = FindViewById<TextView>(Resource.Id.textView2);
            TextView sero = FindViewById<TextView>(Resource.Id.textView4);
            Button button1 = FindViewById<Button>(Resource.Id.button1);
            

            async void getLocation()
            {
                try
                {
                    var location = await Geolocation.GetLastKnownLocationAsync();

                    if (location != null)
                    {
                        garo.Text = $"{location.Longitude}";
                        sero.Text = $"{location.Latitude}"; 
                    }
                    else
                    {
                        garo.Text = $"can't find";
                    }
                }
                catch
                {
                    // Unable to get location
                    garo.Text = "gita dng dng";
                }
            }
            

            button1.Click += (sender, e) => 
            {
                getLocation();
            };
           
        }

            
    }
}