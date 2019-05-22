/*
load dataset("POI/노인복지시설")
load "POI/노인복지시설" splitCount 10

load textFile('교통/지하철/서울역사')
load textFile('교통/지하철/서울역사') splitCount 10

load jdbcTable('subway_stations') {
	url "jdbc:postgresql://129.254.82.95:5433/sbdata"
	user "sbdata"
	password "urc2004"
	driverClass "org.postgresql.Driver"
} options {
	selectExpr "ST_AsBinary(the_geom) as the_geom"
	mapperCount 17
}

storeInto jdbcTable('test') {
	url "jdbc:postgresql://129.254.82.95:5433/sbdata"
	user "sbdata"
	password "urc2004"
	driverClass "org.postgresql.Driver"
}

filter "induty_nm == '경로당'" 
filter "induty_nm == '경로당'", initializer: "__XXX__"

project "the_geom"

defineColumn 'age:double'
defineColumn 'age:double' set "xxxxx"
defineColumn 'age:double' set "xxxxx", initializer: 'YYYY'

update "ENG_SUB_NM = ENG_SUB_NM.toUpperCase()"
update '$money_formatter.parse(거래금액).intValue()', initializer: '$money_formatter = new DecimalFormat("#,###,###")'

expand "age:double,gpa:double" with "xxxx"
expand "age:double,gpa:double" with "xxxx", initializer: "yyy"

pickTop 10 orderBy('age:A')
sort 'age:A'
distinct 'age,distinct' workerCount 10

assignUid 'id'
sample 0.1
shard 10

aggregate { count = count(); total = sum('age'); avg = avg('age') }
aggregate { the_geom = convex_hull 'the_geom' } group keys: 'col1,col2', tags: 'the_geom'
take 10
take 10 group { keys 'col1,col2'; orderBy 'col5:DESC,col6:ASC' }
list all group { keys 'col1,col2'; tags 'col3,col4'; orderBy 'col5:DESC,col6:ASC' }

parseCsv 'text' delim '|' options {
	header "the_geom:point,sig_cd,emd_cd,pnu,age:int,transit_yn:boolean"
	quote '"'
	escape "\\"
	trimColumn false
	nullValue '0'
	throwParseError false
}

hashJoin 'col1,col2', 'POI/노인복지시설', 'col1,jcol2' output 'left.*'
hashJoin 'col1,col2', 'POI/노인복지시설', 'col1,jcol2' output 'left.*' options {
	workerCount 5
}
hashJoin 'col1,col2', 'POI/노인복지시설', 'col1,jcol2' output '*,param.{C1,C2}' options {
	type leftOuter
	workerCount 5
}

def years = 2011..2017
def rightCols = years.collect { "gas_$it" }.join(',')
load hashJoinFile {
	type leftOuter
	left '구역/연속지적도', 'PNU'
	right 'tmp/anyang/gas_side_by_side', 'PNU'
	output "left.*,right.{$rightCols}"
	workerCount 17
}

buffer 'the_geom' distance '1km'
buffer 'the_geom' distance 1000 output 'the_geom2' segmentCount 16

centroid 'the_geom'
centroid 'the_geom' output 'the_geom2'
transformCrs 'the_geom' from 'EPSG:5186' to 'EPSG:4326'

toXY 'the_geom' to 'x_pos', 'y_pos' ignore geometry
toXY 'the_geom' x_col 'x_pos' y_col 'y_pos' keep geometry
toPoint 'the_geom' from 'x_pos', 'y_pos'

filter spatially on 'the_geom' intersects wkt('POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))') negated
filter spatially on 'the_geom' withinDistance '2km' envelope(minx: 0, miny: 0, maxx: 1, maxy:1)

query 'POI/노인복지시설' range envelope(minx: 0, miny: 0, maxx: 1, maxy:1) 
query 'POI/노인복지시설' range wkt('POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))')
query 'POI/노인복지시설' range dataset('xxx') negated

spatialJoin "tmp/10min/high_density_center" output 'the_geom,*-{the_geom}'
spatialJoin "tmp/10min/high_density_center" options {
	geometry 'the_geom'
	output "*,param.col1"
}

spatialSemiJoin "tmp/10min/high_density_center"
spatialSemiJoin "tmp/10min/high_density_center" options {
	geometry 'the_geom'
	joinExpr withinDistance('100m')
	negated true
}

spatialOuterJoin "tmp/10min/high_density_center" output "*,param.col1"
*/

spatialClipJoin "tmp/10min/high_density_hdong"

/*
load squareGrid(bounds: envelope(minx: 10, miny: 20, maxx: 30, maxy:40), cellSize: '10x20')
load squareGrid(bounds: dataset('xxx'), cellSize: '10x20') splitCount 7

assign squareGrid(bounds: envelope(minx: 10, miny: 20, maxx: 30, maxy:40), cellSize: '10x20') to 'the_geom'
assign squareGrid(bounds: dataset('xxx'), cellSize: '10x20') to 'the_geom' ignoreOutside false

storeAsCsv 'tmp/csv' options {
	delim '|'
	quote '"'
	charset 'utf-8'
}
storeAsCsv 'tmp/csv'
*/
