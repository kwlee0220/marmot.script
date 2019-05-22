package marmot.script.command

def methodMissing(String name, args) {
	CommandScriptRunner parser = new CommandScriptRunner(null);
	Closure script = (Closure)args[0]
	script.delegate = parser
	script.resolveStrategy = Closure.DELEGATE_ONLY;
	script()
	parser.run();
}

def test() {
	run {
		create dataset 'tmp/result' {
			geometry column: 'the_geom', srid: 'EPSG:5186'
			force = true
			from plan("create_plan") {
				load '교통/지하철/서울역사'
				filter 'trnsit_yn==1'
			}
		}
		create dataset 'tmp/result' {
			schema {
				the_geom('point')
				age('int')
			}
		}
		
		delete dataset 'tmp/result'
		move dataset 'tmp/result' to 'tmp/result2'
		
		run plan("create_plan") {
			load "POI/노인복지시설"
		}
		
		export dataset 'tmp/result' to shapefile {
			directory 'xxx'
			charset 'euc-kr'
			srid 'EPSG:4326'
		}
		
		append into dataset('tmp/result') from plan("create_plan") {
			load dataset("POI/노인복지시설")
		}
		
		move dataset('tmp/result') to dataset('tmp/result2')
		export dataset('tmp/result') to shapefile('xxxxx') charset 'euc-kr'
	}
}
test()