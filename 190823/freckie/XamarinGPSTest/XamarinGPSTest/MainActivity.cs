using Android.App;
using Android.OS;
using Android.Support.V7.App;
using Android.Runtime;
using Android.Widget;
using Android.Locations;
using System.Collections.Generic;
using Android.Util;
using System.Linq;

namespace XamarinGPSTest
{
    [Activity(Label = "GPS Test", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity, ILocationListener
    {
        TextView textLong;  // TextView for Longitude
        TextView textLat;   // TextView for Latitude
        Location currentLoc;   // Current Location
        LocationManager locManager;
        string locProvider;

        public string TAG
        {
            get;
            private set;
        }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_main);

            textLat = FindViewById<TextView>(Resource.Id.textLat);
            textLong = FindViewById<TextView>(Resource.Id.textLong);
            InitializeLocationManager();
        }

        private void InitializeLocationManager()
        {
            locManager = (LocationManager)GetSystemService(LocationService);
            Criteria criteriaForLocationService = new Criteria
            {
                Accuracy = Accuracy.Fine
            };
            IList<string> acceptableLocationProviders = locManager.GetProviders(criteriaForLocationService, true);

            if (acceptableLocationProviders.Any())
            {
                locProvider = acceptableLocationProviders.First();
            }
            else
            {
                locProvider = string.Empty;
            }
            Log.Debug(TAG, "Using " + locProvider + ".");
        }

        protected override void OnResume()
        {
            base.OnResume();
            locManager.RequestLocationUpdates(locProvider, 0, 0, this);
        }

        protected override void OnPause()
        {
            base.OnPause();
            locManager.RemoveUpdates(this);
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Android.Content.PM.Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        public void OnLocationChanged(Location location)
        {
            currentLoc = location;
            if (currentLoc == null)
            {
                Log.Debug(TAG, "Location Getting Error");
            }
            else
            {
                textLat.Text = currentLoc.Latitude.ToString();
                textLong.Text = currentLoc.Longitude.ToString();
            }
        }

        public void OnProviderDisabled(string provider)
        {
            throw new System.NotImplementedException();
        }

        public void OnProviderEnabled(string provider)
        {
            throw new System.NotImplementedException();
        }

        public void OnStatusChanged(string provider, [GeneratedEnum] Availability status, Bundle extras)
        {
            throw new System.NotImplementedException();
        }
    }
}